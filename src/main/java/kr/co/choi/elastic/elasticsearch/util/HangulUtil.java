package kr.co.choi.elastic.elasticsearch.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HangulUtil {

    private static final char HANGUL_BASE = '가';
    private static final char HANGUL_END = '힣';
    private static final int HANGUL_CHAR_COUNT = HANGUL_END - HANGUL_BASE + 1;
    private static final int CHO_COUNT = 19;
    private static final int JUNG_COUNT = 21;
    private static final int JONG_COUNT = 28;

    private static final char[] CHO = {
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ',
            'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };
    private static final char[] JUNG = {
            'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ',
            'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'
    };
    private static final char[] JONG = {
            ' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ',
            'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };

    public String[] decomposeHangul(String text) {
        try {
            StringBuilder decomposedResult = new StringBuilder();
            StringBuilder chosungResult = new StringBuilder();

            for (char ch : text.toCharArray()) {
                if (isHangul(ch)) {
                    int code = ch - HANGUL_BASE;
                    int cho = code / (JUNG_COUNT * JONG_COUNT);
                    int jung = (code % (JUNG_COUNT * JONG_COUNT)) / JONG_COUNT;
                    int jong = code % JONG_COUNT;

                    // 자소 분리된 결과
                    decomposedResult.append(CHO[cho]).append(JUNG[jung]);
                    if (jong > 0) {
                        decomposedResult.append(JONG[jong]);
                    }

                    // 초성만 추가
                    chosungResult.append(CHO[cho]);
                } else {
                    // 한글이 아닌 문자는 그대로 추가
                    decomposedResult.append(ch);
                    chosungResult.append(ch);
                }
            }

            return new String[]{decomposedResult.toString(), chosungResult.toString()};
        } catch (Exception e) {
            log.error("Error in decomposeHangul: ", e);
            return new String[]{text, text}; // 오류 발생 시 원본 텍스트 반환
        }
    }

    // 초성 필드만 추출하는 메소드
    public String extractChosung(String text) {
        StringBuilder result = new StringBuilder();
        for (char ch : text.toCharArray()) {
            if (isHangul(ch)) {
                int code = ch - HANGUL_BASE;
                int cho = code / (JUNG_COUNT * JONG_COUNT);
                result.append(CHO[cho]);
            } else {
                result.append(ch); // 한글이 아닌 문자는 그대로 추가
            }
        }
        return result.toString();
    }

    private boolean isHangul(char ch) {
        return ch >= HANGUL_BASE && ch <= HANGUL_END;
    }

    private String decompose(char hangul) {
        int code = hangul - HANGUL_BASE;
        int cho = code / (JUNG_COUNT * JONG_COUNT);
        int jung = (code % (JUNG_COUNT * JONG_COUNT)) / JONG_COUNT;
        int jong = code % JONG_COUNT;

        StringBuilder decomposed = new StringBuilder();
        decomposed.append(CHO[cho]);
        decomposed.append(JUNG[jung]);
        if (jong > 0) {
            decomposed.append(JONG[jong]);
        }
        return decomposed.toString();
    }
}