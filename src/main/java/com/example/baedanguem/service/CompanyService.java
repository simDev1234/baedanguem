package com.example.baedanguem.service;

import com.example.baedanguem.model.Company;
import com.example.baedanguem.model.ScrapedResult;
import com.example.baedanguem.persist.CompanyRepository;
import com.example.baedanguem.persist.DividendRepository;
import com.example.baedanguem.persist.entity.CompanyEntity;
import com.example.baedanguem.persist.entity.DividendEntity;
import com.example.baedanguem.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.var;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    private final Trie trie;

    private final Scraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;

    private final DividendRepository dividendRepository;

    public Company save(String ticker) {

        boolean exists = this.companyRepository.existsByTicker(ticker);

        if (exists) {
            throw new RuntimeException("already exists ticker -> " + ticker);
        }

        return this.storeCompanyAndDividend(ticker);
    }

    private Company storeCompanyAndDividend(String ticker) {

        // ticker를 기준으로 회사를 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);

        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("failed to scrap ticker -> " + ticker);
        }

        // 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

        // 스크래핑 결과
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));

        List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
                .map(e -> new DividendEntity(companyEntity.getId() , e))
                .collect(Collectors.toList());

        this.dividendRepository.saveAll(dividendEntities);

        return company;
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable){
        return this.companyRepository.findAll(pageable);
    }

    // 자동완성 - Trie에 단어 추가
    public void addAutocompleteKeyword(String keyword){
        this.trie.put(keyword, null); // 아파치의 trie의 경우 key에 단어뿐 아니라, 추가적으로 value도 넣어줄 수 있도록 되어 있다.
    }

    // 자동완성 - Trie에서 접두사가 일치하는 단어 리스트 가져오기
    public List<String> autocomplete(String keyword){
        return (List<String>) this.trie.prefixMap(keyword).keySet().stream().collect(Collectors.toList());
    }

    // 자동완성 - Trie에서 단어 삭제
    public void deleteAutocompleteKeyword(String keyword){
        this.trie.remove(keyword);
    }

    // 자동완성 - SQL like 사용
    public List<String> getCompanyNamesByKeyword(String keyword){

        Pageable limit = PageRequest.of(0, 10);
        Page<CompanyEntity> companyEntities = companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);

        return companyEntities.stream()
                                .map(e -> e.getName())
                                .collect(Collectors.toList());

    }

    public String deleteCompany(String ticker) {

        var company = this.companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사입니다."));

        this.dividendRepository.deleteAllByCompanyId(company.getId());
        this.companyRepository.delete(company);

        this.deleteAutocompleteKeyword(company.getName());

        return company.getName();
    }
}
