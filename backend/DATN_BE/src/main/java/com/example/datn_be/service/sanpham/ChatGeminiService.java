package com.example.datn_be.service.sanpham;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
public class ChatGeminiService {
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-001:generateContent";
    private static final String GEMINI_API_KEY = "AIzaSyADle3z5HuxqqA35yGhHZioq-JiVOuYPVs";

    @Autowired
    private SanPhamService sanPhamService; // Service để lấy dữ liệu sản phẩm

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String askGemini(String prompt) {
        try {
            return callGeminiAPI(prompt);
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi khi gọi API Gemini: " + e.getMessage();
        }
    }

    public String getProductAdvice(String customerQuery) {
        try {
            List<Map<String, Object>> products = sanPhamService.getAllProducts();
            List<Map<String, Object>> topSellingProducts = sanPhamService.getTopProductsSale();

            String topSellingProductsJson = objectMapper.writeValueAsString(topSellingProducts);
            String productsJson = objectMapper.writeValueAsString(products);

            String prompt = createProductAdvicePrompt(productsJson,topSellingProductsJson, customerQuery);

            return callGeminiAPI(prompt);

        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi khi tư vấn sản phẩm: " + e.getMessage();
        }
    }

    private String createProductAdvicePrompt(String productsJson, String productTopsale, String customerQuery) {
        return "🌟 Chào mừng đến với Graceful! 🌟\n" +
                "Bạn yêu quý ơi, hãy hóa thân thành một chuyên gia áo dài có tâm – có tầm – lại hài hước, dễ thương kiểu 'Graceful' nha! 😉\n\n" +
                "Dưới đây là danh sách các mẫu áo dài của nhà Graceful:\n" +
                productsJson + "\n\n" +
                "🔥 Top 5 mẫu 'cháy đơn' nhất:\n" +
                productTopsale + "\n\n" +
                "Khách hàng đang quan tâm đến: \"" + customerQuery + "\" 💭\n\n" +
                "Với con mắt fashion 'thần sầu', hãy gợi ý một vài mẫu phù hợp theo định dạng sau (tổng dưới 300 ký tự, trình bày HTML mượt mà, không div/table/** nha):\n\n" +
                "✅ <b>Bố cục mô tả sản phẩm:</b>\n" +
                "-🏷️ <b>Tên sản phẩm</b> – Mã sản phẩm, Chất liệu<br>, Loại áo dài <br>, Giá bán<br>\n\n" +
                "-🎨 Màu sắc: – 📏 Kích thước còn hàng<br>\n" +
                "-🖼️ Ảnh sản phẩm: 1–2 ảnh sản phẩm minh họa<br>\n" +
                "-🔗 <a href=\"/san-pham/{id}\">Xem sản phẩm</a><br>\n" +
                "✨ Ưu tiên gợi ý sản phẩm 'mới về' hoặc 'bán chạy'. Nếu có khuyến mãi đang hoạt động (ngayBatDau, ngayKetThuc, trangThaiKhuyenMai == true), thì khéo léo rủ rê khách nha!\n" +
                "💖 Đừng quên giữ văn phong dễ thương, xì tin, thêm emoji để tăng 'duyên dáng'. Hạn chế dùng <br> quá nhiều dòng.\n\n" +
                "💌 Graceful luôn đồng hành để bạn chọn được chiếc áo dài 'chân ái' của mình! 🌸";
    }


    private String callGeminiAPI(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Tạo JSON body
            JSONObject textPart = new JSONObject();
            textPart.put("text", prompt);

            JSONArray partsArray = new JSONArray();
            partsArray.put(textPart);

            JSONObject contentObject = new JSONObject();
            contentObject.put("parts", partsArray);

            JSONArray contentsArray = new JSONArray();
            contentsArray.put(contentObject);

            JSONObject body = new JSONObject();
            body.put("contents", contentsArray);

            // Thêm cấu hình để xử lý prompt dài hơn
            body.put("generationConfig", new JSONObject()
                    .put("maxOutputTokens", 2048)
                    .put("temperature", 0.7));

            HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);
            RestTemplate restTemplate = new RestTemplate();

            // Gửi request đến API Gemini
            ResponseEntity<String> response = restTemplate.exchange(
                    GEMINI_API_URL + "?key=" + GEMINI_API_KEY,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            String responseBody = response.getBody();

            // Xử lý phản hồi từ API Gemini
            JSONObject responseObject = new JSONObject(responseBody);

            JSONArray candidates = responseObject.getJSONArray("candidates");
            if (candidates.length() > 0) {
                JSONObject firstCandidate = candidates.getJSONObject(0);
                if (firstCandidate.has("content")) {
                    JSONObject content = firstCandidate.getJSONObject("content");
                    if (content.has("parts")) {
                        JSONArray parts = content.getJSONArray("parts");
                        if (parts.length() > 0) {
                            JSONObject firstPart = parts.getJSONObject(0);
                            if (firstPart.has("text")) {
                                return firstPart.getString("text");
                            }
                        }
                    }
                }
            }

            return "Không tìm thấy nội dung phản hồi hợp lệ từ Gemini.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi khi gọi API Gemini: " + e.getMessage();
        }
    }
}