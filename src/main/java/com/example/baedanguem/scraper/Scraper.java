package com.example.baedanguem.scraper;

import com.example.baedanguem.model.Company;
import com.example.baedanguem.model.ScrapedResult;

public interface Scraper {

    Company scrapCompanyByTicker(String ticker);

    ScrapedResult scrap(Company company);

}
