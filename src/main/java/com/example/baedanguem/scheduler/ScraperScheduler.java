package com.example.baedanguem.scheduler;

import com.example.baedanguem.model.Company;
import com.example.baedanguem.model.ScrapedResult;
import com.example.baedanguem.model.constants.CacheKey;
import com.example.baedanguem.persist.CompanyRepository;
import com.example.baedanguem.persist.DividendRepository;
import com.example.baedanguem.persist.entity.CompanyEntity;
import com.example.baedanguem.persist.entity.DividendEntity;
import com.example.baedanguem.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@AllArgsConstructor
@EnableCaching
@Slf4j
public class ScraperScheduler {

    private final CompanyRepository companyRepository;

    private final Scraper yahooFinanceScraper;

    private final DividendRepository dividendRepository;

    // 일정 주기마다 수행
    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {

        //log.info("scraping scheduler is started");

        // 저장된 회사 목록 조회
        List<CompanyEntity> companies = companyRepository.findAll();

        // 회사마다 배당금 정보를 새로 스크래핑
        for (var company : companies) {
            log.info("scraping scheduler is started -> " + company.getName());
            ScrapedResult scrapedResult = yahooFinanceScraper.scrap(new Company(company.getTicker(), company.getName()));

            // 스크래핑한 배당금 정보 중 데이터베이스에 없는 값은 저장
            scrapedResult.getDividends().stream()
                    // 디비든 모델을 디비든 엔터티로 매핑
                    .map(e -> new DividendEntity(company.getId(), e))
                    // 엘리먼트를 하나씩 디비든 레파지토리에 삽입
                    .forEach(e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if (!exists) {
                            this.dividendRepository.save(e);
                        }
                    });

            // (DB에 부하가 가지 않도록) 연속적으로 스크패핑 대상 사이트 서버에 요청을 날리지 않도록
            try {
                Thread.sleep(3000); // 3 seconds
            } catch (InterruptedException e) { // 인터럽트를 받는 스레드가 blocking 될 수 있는 메소드를 실행할 때 발생
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }

        }

    }

}
