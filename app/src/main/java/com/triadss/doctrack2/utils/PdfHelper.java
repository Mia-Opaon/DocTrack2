package com.triadss.doctrack2.utils;
//
//import static java.lang.Math.ceil;
//
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Typeface;
//import android.graphics.pdf.PdfDocument;
//import android.os.Build;
//import android.os.Environment;
//import android.text.Layout;
//import android.text.StaticLayout;
//import android.text.TextPaint;
//import android.util.DisplayMetrics;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.triadss.doctrack2.R;
//import com.triadss.doctrack2.activity.healthprof.adapters.HealthProfessionalReportAdapter;
//import com.triadss.doctrack2.config.constants.PdfConstants;
//import com.triadss.doctrack2.dto.AddPatientDto;
//import com.triadss.doctrack2.dto.DateTimeDto;
//import com.triadss.doctrack2.dto.ReportDto;
//import com.triadss.doctrack2.types.Vector2i;
//
//import org.w3c.dom.Text;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.util.Vector;
//import java.util.function.Consumer;
//
//public class PdfHelper {
//    public static void GeneratePdfFromReports(Context context,
//                                              String fileName,
//                                              List<ReportDto> reportList, String patientInfo, Consumer<File> afterCreate) {
//        int pageWidth = PdfConstants.LETTER_PAGE_WIDTH;
//        int pageHeight = PdfConstants.LETTER_PAGE_HEIGHT;
//
//        PdfDocument pdf = new PdfDocument();
//
//        /**Find total page required for the pdf**/
//        int totalPage = getRequiredPages(reportList.size());
//        int pageNum = 0;
//
//        /**If the total item is less than pageCapacity then we take the item size as new capacity**/
//        int finalPageCapacity = Math.min(PdfConstants.REPORTS_PER_PAGE, reportList.size());
//
//        /**Configure Positioning in px**/
//        int margin = 20;
//        int topMargin = 40;
//        int textSize = 12;
//        //Width per report
//        int width = pageWidth - (2 * margin); //2 for both sides
//        int columnWidth = (pageWidth - 2 * margin) / PdfConstants.NUM_COLUMNS;
//        //Label Distance
//        int labelDistance = 100;
//        //Vertical Distance before next Report
//        int verticalDistance = 25;
//        Vector2i currentPos = new Vector2i(margin, topMargin);
//
//        /**Canvas Config**/
//        Paint textPaint = new Paint();
//        textPaint.setTextSize(textSize);
//        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
//        textPaint.setColor(ContextCompat.getColor(context, R.color.black));
//
//        Paint textPaintBold = new Paint();
//        textPaintBold.setTextSize(textSize);
//        textPaintBold.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
//        textPaintBold.setColor(ContextCompat.getColor(context, R.color.black));
//
//        /**We paginated the list and add them to parentView**/
//        while (pageNum < totalPage) {
//            List<ReportDto> currentPageItems = reportList.subList(
//                    pageNum * finalPageCapacity,
//                    Math.min(++pageNum * finalPageCapacity, reportList.size())
//            );
//            currentPos = new Vector2i(margin, topMargin);
//
//            /**After adding items to view we render the new page and add it to pdf **/
//            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo
//                    .Builder(pageWidth, pageHeight, pageNum)
//                    .create();
//            PdfDocument.Page dynamicPage = pdf.startPage(pageInfo);
//            Canvas canvas = dynamicPage.getCanvas();
//
//            // Draw patient information
//            canvas.drawText("Patient Information:", currentPos.getX(), currentPos.getY(), textPaintBold);
//            currentPos = currentPos.withY(currentPos.getY() + textSize);
//            canvas.drawText(patientInfo, currentPos.getX(), currentPos.getY(), textPaint);
//            currentPos = currentPos.withY(currentPos.getY() + textSize * 2); // Add some space
//
//
//            String[] columnHeaders = {"Action", "Date", "Message"};
//            for (int i = 0; i < columnHeaders.length; i++) {
//                canvas.drawText(columnHeaders[i], currentPos.getX() + i * columnWidth, currentPos.getY(), textPaintBold);
//            }
//
//            currentPos = currentPos.withY(currentPos.getY() + textSize);
//
//            for (ReportDto reportDto : currentPageItems) {
//                // Calculate current row position
//                int currentY = currentPos.getY();
//                DateTimeDto dateTime = DateTimeDto.ToDateTimeDto(reportDto.getCreatedDate());
//
//                // Action
//                String action = reportDto.getAction();
//                currentY = drawMultilineText(canvas, textPaint, action, currentPos.getX(), currentY, columnWidth, textSize);
//
//                // Date
//                String date = dateTime.formatDateTime();
//                currentY = drawMultilineText(canvas, textPaint, date, currentPos.getX() + columnWidth, currentPos.getY(), columnWidth, textSize);
//
//                // Description
//                String description = reportDto.getMessage();
//                currentY = drawMultilineText(canvas, textPaint, description, currentPos.getX() + 2 * columnWidth, currentPos.getY(), columnWidth, textSize);
//
//                currentPos = currentPos.withY(currentY);
//            }
//
//            pdf.finishPage(dynamicPage);
//        }
//
//        //Export
//        // below line is used to set the name of
//        // our PDF file and its path.
//        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
//                fileName + ".pdf");
//
//        try {
//            // after creating a file name we will
//            // write our PDF file to that location.
//            pdf.writeTo(Files.newOutputStream(file.toPath()));
//            // below line is to print toast message
//            // on completion of PDF generation.
//            Toast.makeText(context, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
//            afterCreate.accept(file);
//        } catch (IOException e) {
//            // below line is used
//            // to handle error
//            e.printStackTrace();
//        }
//        // after storing our pdf to that
//        // location we are closing our PDF file.
//        pdf.close();
//    }
//
//    private static int drawMultilineText(Canvas canvas, Paint paint, String text, int x, int y, int maxWidth, int textSize) {
//        // Split text into lines that fit within the maxWidth
//        List<String> lines = new ArrayList<>();
//        TextPaint textPaint = new TextPaint(paint);
//        StaticLayout layout = new StaticLayout(text, textPaint, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
//        int lineCount = layout.getLineCount();
//        for (int i = 0; i < lineCount; i++) {
//            int lineStart = layout.getLineStart(i);
//            int lineEnd = layout.getLineEnd(i);
//            String line = text.substring(lineStart, lineEnd);
//            lines.add(line);
//        }
//
//        // Draw each line
//        for (String line : lines) {
//            canvas.drawText(line, x, y, paint);
//            y += textSize; // Move to the next line
//        }
//
//        // Return the Y position after drawing all lines
//        return y;
//    }
//    private static int getRequiredPages(int totalItems) {
//        return (int)ceil((double) totalItems / PdfConstants.REPORTS_PER_PAGE);
//    }
//}
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Toast;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.triadss.doctrack2.R;
import com.triadss.doctrack2.config.constants.PdfConstants;
import com.triadss.doctrack2.dto.ReportDto;
import com.triadss.doctrack2.dto.DateTimeDto;
import com.triadss.doctrack2.types.Vector2i;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class PdfHelper {

    public static void GeneratePdfFromReports(Context context,
                                              String fileName,
                                              List<ReportDto> reportList, String patientInfo, Consumer<File> afterCreate) {

        // Output file path
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + fileName + ".pdf";

        try {
            // Initialize PDF writer
            PdfWriter writer = new PdfWriter(filePath);

            // Initialize PDF document
            PdfDocument pdf = new PdfDocument(writer);

            // Initialize document
            Document document = new Document(pdf, PageSize.LETTER);
            // Get the image from drawable and convert it to a byte array
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logoo);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            ImageData imageData = ImageDataFactory.create(byteArray);
            Image pdfImage = new Image(imageData);

            // Adjust image size (optional)
            float imageWidth = 50; // Example width
            float imageHeight = 50; // Example height
            pdfImage.scaleToFit(imageWidth, imageHeight);

            document.add((pdfImage).setHorizontalAlignment(HorizontalAlignment.CENTER));

            document.add(new Paragraph("Reports").setBold().setFontSize(14).setTextAlignment(TextAlignment.CENTER));

            // Add patient information
            document.add(new Paragraph("Patient Information").setBold());
            document.add(new Paragraph(patientInfo));

            // Add table headers
            Table table = new Table(new float[]{1, 1, 1}).useAllAvailableWidth();
            table.addCell(new Cell().add(new Paragraph("Action").setBold()));
            table.addCell(new Cell().add(new Paragraph("Date").setBold()));
            table.addCell(new Cell().add(new Paragraph("Details").setBold()));

            // Add data to the table
            for (ReportDto reportDto : reportList) {
                DateTimeDto dateTime = DateTimeDto.ToDateTimeDto(reportDto.getCreatedDate());
                table.addCell(new Cell().add(new Paragraph(reportDto.getAction())));
                table.addCell(new Cell().add(new Paragraph(dateTime.formatDateTime())));
                table.addCell(new Cell().add(new Paragraph(reportDto.getMessage())));
            }

            // Add table to the document
            document.add(table);

            // Close document
            document.close();

            // Show success message
            Toast.makeText(context, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();

            // Execute callback
            afterCreate.accept(new File(filePath));

        } catch (IOException e) {
            // Handle exception
            e.printStackTrace();
            Toast.makeText(context, "Error generating PDF file.", Toast.LENGTH_SHORT).show();
        }
    }
}
