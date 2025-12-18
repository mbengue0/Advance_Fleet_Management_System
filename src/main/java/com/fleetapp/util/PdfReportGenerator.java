package com.fleetapp.util;

import com.fleetapp.model.Vehicle;
import com.fleetapp.model.VehicleStatus;

// --- SPECIFIC OPENPDF IMPORTS ---
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle; // <--- Explicitly use the PDF Rectangle
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;

// --- SPECIFIC JAVA AWT IMPORT ---
import java.awt.Color; // <--- Only import Color, NOT the whole package (*)

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
public class PdfReportGenerator {

    // Define Corporate Colors
    private static final Color BRAND_COLOR = new Color(44, 62, 80); // Dark Blue #2c3e50
    private static final Color ACCENT_COLOR = new Color(236, 240, 241); // Light Grey

    public static void generateVehicleReport(List<Vehicle> vehicleList, File file) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(file));

        document.open();

        // 1. THE HEADER (Identity)
        addHeader(document);

        // 2. EXECUTIVE SUMMARY (Manager View)
        addExecutiveSummary(document, vehicleList);

        // 3. THE DATA TABLE (Details)
        addVehicleTable(document, vehicleList);

        // 4. THE FOOTER
        addFooter(document);

        document.close();
    }

    private static void addHeader(Document doc) throws DocumentException {
        // Create a 2-column table for the header (Left: Title, Right: Date)
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);

        // Company Name / Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BRAND_COLOR);
        PdfPCell titleCell = new PdfPCell(new Phrase("FLEET MANAGER PRO", titleFont));
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerTable.addCell(titleCell);

        // Date & Context
        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        Paragraph datePara = new Paragraph("Inventory Report\n" + dateStr + "\n" + timeStr, dateFont);
        datePara.setAlignment(Element.ALIGN_RIGHT);

        PdfPCell dateCell = new PdfPCell(datePara);
        dateCell.setBorder(Rectangle.NO_BORDER);
        dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        headerTable.addCell(dateCell);

        doc.add(headerTable);

        // Add a nice divider line
        doc.add(new Paragraph(" "));
        LineSeparator ls = new LineSeparator();
        ls.setLineColor(Color.LIGHT_GRAY);
        doc.add(ls);
        doc.add(new Paragraph(" "));
    }

    private static void addExecutiveSummary(Document doc, List<Vehicle> list) throws DocumentException {
        // Calculate Stats
        long total = list.size();
        long available = list.stream().filter(v -> v.getStatus() == VehicleStatus.AVAILABLE).count();
        long maintenance = list.stream().filter(v -> v.getStatus() == VehicleStatus.MAINTENANCE).count();
        long onTrip = list.stream().filter(v -> v.getStatus() == VehicleStatus.ON_TRIP).count();

        // Title
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BRAND_COLOR);
        doc.add(new Paragraph("Executive Summary", sectionFont));
        doc.add(new Paragraph(" "));

        // Summary Table
        PdfPTable summaryTable = new PdfPTable(4);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingAfter(20);

        addSummaryCell(summaryTable, "Total Fleet", String.valueOf(total));
        addSummaryCell(summaryTable, "Available", String.valueOf(available));
        addSummaryCell(summaryTable, "On Trip", String.valueOf(onTrip));
        addSummaryCell(summaryTable, "Maintenance", String.valueOf(maintenance));

        doc.add(summaryTable);
    }

    private static void addSummaryCell(PdfPTable table, String label, String value) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(ACCENT_COLOR);
        cell.setPadding(10);
        cell.setBorder(Rectangle.NO_BORDER);

        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BRAND_COLOR);

        Paragraph pLabel = new Paragraph(label.toUpperCase(), labelFont);
        Paragraph pValue = new Paragraph(value, valueFont);
        pLabel.setAlignment(Element.ALIGN_CENTER);
        pValue.setAlignment(Element.ALIGN_CENTER);

        cell.addElement(pLabel);
        cell.addElement(pValue);

        // Add a small spacer column effect by using border
        cell.setBorderWidthRight(2);
        cell.setBorderColorRight(Color.WHITE);

        table.addCell(cell);
    }

    private static void addVehicleTable(Document doc, List<Vehicle> list) throws DocumentException {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BRAND_COLOR);
        doc.add(new Paragraph("Detailed Inventory", sectionFont));
        doc.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(5); // 5 Columns
        table.setWidthPercentage(100);
        // Set column relative widths (ID is small, Plate is medium, etc.)
        table.setWidths(new float[]{1f, 2f, 2f, 2f, 2f});

        // --- Header Row ---
        String[] headers = {"ID", "License Plate", "Brand", "Model", "Status"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE)));
            cell.setBackgroundColor(BRAND_COLOR);
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // --- Data Rows ---
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

        boolean alternate = false; // For Zebra striping
        for (Vehicle v : list) {
            addStyledCell(table, String.valueOf(v.getId()), alternate, dataFont);
            addStyledCell(table, v.getLicensePlate(), alternate, dataFont);
            addStyledCell(table, v.getBrand(), alternate, dataFont);
            addStyledCell(table, v.getModel(), alternate, dataFont);
            addStyledCell(table, v.getStatus().toString(), alternate, dataFont);

            alternate = !alternate; // Flip color
        }

        doc.add(table);
    }

    private static void addStyledCell(PdfPTable table, String text, boolean isAlternate, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        // Zebra Striping (Light Grey vs White)
        if (isAlternate) {
            cell.setBackgroundColor(new Color(245, 245, 245));
        } else {
            cell.setBackgroundColor(Color.WHITE);
        }

        table.addCell(cell);
    }

    private static void addFooter(Document doc) throws DocumentException {
        // Push footer to bottom
        // Note: Simple footer implementation for single page context
        doc.add(new Paragraph("\n\n"));
        LineSeparator ls = new LineSeparator();
        ls.setLineColor(Color.LIGHT_GRAY);
        doc.add(ls);

        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, Color.GRAY);
        Paragraph footer = new Paragraph("Confidential Document | Generated by Fleet Management System | Internal Use Only", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(10);
        doc.add(footer);
    }
}