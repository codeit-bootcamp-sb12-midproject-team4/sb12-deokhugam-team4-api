package com.codeit.deokhugam.domain.book;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AladinIsbnHarvester {

	public static void main(String[] args) throws Exception {

		String ttbKey = System.getenv("ALADIN_API_CLIENT_SECRET");
		if (ttbKey == null || ttbKey.isBlank()) {
			System.out.println("❌ 환경변수 ALADIN_API_CLIENT_SECRET가 설정되지 않았습니다.");
			return;
		}

		// 🎯 확실하게 수천~수만 권씩 박혀있는 알라딘 핵심 대분류/중분류 25선
		int[] categories = {
			1,     // 소설/시/희곡
			170,   // 경제경영
			336,   // 자기계발
			656,   // 인문학
			798,   // 사회과학
			74,    // 역사
			987,   // 과학
			351,   // 컴퓨터/모바일
			517,   // 예술/대중문화
			1322,  // 국어/외국어
			2105,  // 에세이
			55890, // 건강/취미
			1196,  // 여행
			1230,  // 종교/역학
			2551,  // 만화
			2913,  // 잡지
			8257,  // 대학교재/전문서적
			1108,  // 어린이
			13789, // 청소년
			51038, // 요리/살림
			50246, // 전집/중고도서 계열
			51130, // 장르소설
			51206, // 희곡/시나리오
			51123, // 역사소설
			53511  // IT 자격증/수험서
		};

		// 🚀 수집 밀도를 4배로 올릴 쿼리 타입 조합
		String[] queryTypes = {"ItemNewAll", "ItemNewSpecial", "Bestseller", "BlogBest"};

		Set<String> isbnSet = new LinkedHashSet<>();
		// 301 에러 방지를 위해 followRedirects 설정 추가
		HttpClient client = HttpClient.newBuilder()
			.followRedirects(HttpClient.Redirect.NORMAL)
			.build();

		System.out.println("🔥 [전략 변경] 25개 핵심 분류 X 4개 채널 융단폭격을 시작합니다...");
		long startTime = System.currentTimeMillis();
		int totalSaved = 0;

		// 대용량 쓰기를 위한 BufferedWriter 오픈 (스트리밍 방식)
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("isbns_100000.json"))) {
			writer.write("{\n  \"isbns\": [\n");
			boolean isFirst = true;

			for (int cid : categories) {
				for (String qType : queryTypes) {
					int foundInLoop = 0;

					for (int page = 1; page <= 20; page++) {
						String url = String.format(
							"https://www.aladin.co.kr/ttb/api/ItemList.aspx?ttbkey=%s&QueryType=%s&MaxResults=50&start=%d&SearchTarget=Book&output=js&CategoryId=%d&Version=20131101",
							ttbKey, qType, page, cid);

						try {
							HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
							HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

							// 정규식 매칭 (혹시 모를 공백 대응 패턴 적용)
							Matcher m = Pattern.compile("\"isbn13\"\\s*:\\s*\"(\\d{13})\"").matcher(response.body());
							int foundInPage = 0;

							while (m.find()) {
								String isbn = m.group(1);
								if (isbnSet.add(isbn)) { // 중복 제거 통과 시
									if (!isFirst) {
										writer.write(",\n");
									}
									writer.write("    \"" + isbn + "\"");
									isFirst = false;
									foundInPage++;
									foundInLoop++;
									totalSaved++;
								}
							}

							// 해당 페이지에 더 이상 새로운 책이 없으면 다음 쿼리로 조기 이동
							if (foundInPage == 0) break;

							// 디버깅용 실시간 스케일 체커
							if (totalSaved % 1000 == 0 && foundInPage > 0) {
								System.out.printf("🚀 [실시간 통계] 누적 유니크 ISBN 확보: %d개... 진행 중%n", totalSaved);
							}

							Thread.sleep(100); // 디바이스 및 서버 보호용 0.1초 딜레이

						} catch (Exception e) {
							System.err.printf("❌ 에러 발생 (CID: %d, Type: %s, Page: %d) -> 스킵 후 계속 진행%n", cid, qType, page);
						}
					}
					if (foundInLoop > 0) {
						System.out.printf("✅ 분류 [%d] - 채널 [%s] 완료. 유니크 수집: %d권 | 총 누적: %d개%n",
							cid, qType, foundInLoop, totalSaved);
					}
				}
			}
			writer.write("\n  ]\n}");
		} catch (IOException e) {
			System.err.println("❌ 파일 기록 중 치명적 에러: " + e.getMessage());
		}

		long endTime = System.currentTimeMillis();
		System.out.printf("%n🎉 [수집 완벽 완료] 총 %d개의 중복 없는 고품질 ISBN 데이터를 'isbns_100000.json'에 저장했습니다! (소요 시간: %d초)%n",
			totalSaved, (endTime - startTime) / 1000);
	}
}
