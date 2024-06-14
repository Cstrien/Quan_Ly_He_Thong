 package Controller;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

public class ClipboardReader {

    public String readClipboard() {
        try {
            // Lấy đối tượng Clipboard từ Toolkit
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            
            // Lấy dữ liệu trong clipboard dưới dạng Transferable
            Transferable content = clipboard.getContents(null);

            // Kiểm tra xem dữ liệu có phải là dạng string không
            if (content != null && content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                // Chuyển đổi dữ liệu sang chuỗi và trả về
                return (String) content.getTransferData(DataFlavor.stringFlavor);
            } else {
                // Nếu dữ liệu không phải là dạng chuỗi, trả về null hoặc một thông báo lỗi
                return "No data in clipboard or unsupported data type.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error retrieving clipboard data.";
        }
    }
}
