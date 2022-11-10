package com.example.baedanguem.service;

import com.example.baedanguem.model.Company;
import com.example.baedanguem.model.Dividend;
import com.example.baedanguem.model.ScrapedResult;
import com.example.baedanguem.persist.CompanyRepository;
import com.example.baedanguem.persist.DividendRepository;
import com.example.baedanguem.persist.entity.CompanyEntity;
import com.example.baedanguem.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import lombok.var;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;

    private final DividendRepository dividendRepository;

    public ScrapedResult getDividendByCompanyName(String companyName) {

        // 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명입니다."));

        // 2. 조회된 회사 id로 배당금 정보를 조회
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());

        // 3. 결과 조합 후 반환
        List<Dividend> dividends = new ArrayList<>();

        for (var entity : dividendEntities) {

            dividends.add(Dividend.builder()
                    .date(entity.getDate())
                    .dividend(entity.getDividend())
                    .build());

        }

        return new ScrapedResult(
                Company.builder()
                        .ticker(company.getTicker())
                        .name(company.getName())
                        .build(),
                dividends
        );

    }

}
