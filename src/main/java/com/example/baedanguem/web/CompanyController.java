package com.example.baedanguem.web;

import com.example.baedanguem.model.Company;
import com.example.baedanguem.persist.entity.CompanyEntity;
import com.example.baedanguem.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String keyword) {

        // 자동완성 리스트
        var result = this.companyService.getCompanyNamesByKeyword(keyword);

        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<?> searchCompany(final Pageable pageable) {

        Page<CompanyEntity> companies = this.companyService.getAllCompany(pageable);

        return ResponseEntity.ok(companies);

    }

    @PostMapping
    public ResponseEntity<?> addCompany(@RequestBody Company request){

        String ticker = request.getTicker().trim();

        if (ObjectUtils.isEmpty(ticker)) {
            throw new RuntimeException("ticker is empty");
        }

        Company company = this.companyService.save(ticker);

        this.companyService.addAutocompleteKeyword(company.getName()); // 자동완성 키워드 추가

        return ResponseEntity.ok(company);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCompany(){
        return null;
    }

}
