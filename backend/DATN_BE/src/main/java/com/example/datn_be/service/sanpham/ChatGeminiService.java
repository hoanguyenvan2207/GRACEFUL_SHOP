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

            String topSellingProductsJson = objectMapper.writeValueAsString(products);
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
                "Bạn yêu quý ơi, hãy hóa thân thành một chuyên gia áo dài có tâm, có tầm, lại thêm chút hài hước tinh tế của Graceful nha! 😉\n\n" +
                "Đây là kho tàng áo dài xinh xắn của chúng mình:\n" +
                productsJson + "\n\n" +
                "Top 5 'em' áo dài đang được 'săn đón' nhất tại Graceful:\n" +
                productTopsale + "\n\n" +
                "Khách hàng đang 'say nắng' chiếc áo dài có đặc điểm: \"" + customerQuery + "\"\n\n" +
                "Với 'radar' thời trang siêu nhạy bén, hãy tư vấn cho khách những mẫu áo dài 'xinh lung linh', tập trung vào: <0xF0><0x9F><0x91><0xAB> <b>Kiểu dáng</b> (truyền thống, cách tân,...), ✨ <b>Chất liệu</b> (lụa, gấm, voan,...), và 💰 <b>Giá cả</b> (thật 'iu thương' nha!).\n" +
                "Nhớ nha, thông tin 'ngắn mà chất' (dưới 300 ký tự) và 'show hàng' dưới dạng HTML liền mạch (không div, không table rườm rà!).\n" +
                "🎁 Nếu Graceful đang có 'deal hời' nào (kiểm tra ngayBatDau, ngayKetThuc, trangThaiKhuyenMai), thì 'rủ rê' khách liền nha! Chỉ 'bật mí' khuyến mãi khi còn 'hoạt động' và trạng thái là 'true' thôi đó!\n" +
                "💖 Gợi ý những 'bé' áo dài cụ thể với 🏷️ <b>Tên gọi</b>, 🎨 <b>Màu sắc</b>, và 📏 <b>Kích thước</b> có sẵn (trangThai = true). Ưu tiên 'new arrival' và 'best seller' để khách 'chốt đơn' liền tay!\n" +
                "🚫 Tuyệt đối không dùng **, *, in đậm thông tin sản phẩm một cách 'cứng nhắc', mà hãy dùng icon 'cute phô mai que' và ngôn ngữ 'xì tin' để tư vấn thêm phần 'duyên dáng' nha! Hạn chế 'nhảy dòng' <br> quá nhiều nữa!\n\n" +
                "💌 Graceful luôn ở đây để 'se duyên' cho bạn tìm được chiếc áo dài 'định mệnh' đó! Chúc bạn một ngày thật 'tươi như hoa' nhé! 🌸";
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