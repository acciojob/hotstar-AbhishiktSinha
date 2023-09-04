package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.SubscriptionType;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Don't forget to save the production and webseries Repo

        // check for name duplicacy
        String name = webSeriesEntryDto.getSeriesName();
        if(webSeriesRepository.findBySeriesName(name) != null) {
            throw new Exception("Series is already present");
        }

        // production house null check
        Optional<ProductionHouse> optionalProductionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId());
        if(!optionalProductionHouse.isPresent()){
            throw new Exception("Production house is not present");
        }

        //create WebSeries object
        ProductionHouse productionHouse = optionalProductionHouse.get();
        int ageLimit = webSeriesEntryDto.getAgeLimit();
        double rating = webSeriesEntryDto.getRating();
        SubscriptionType subscriptionType = webSeriesEntryDto.getSubscriptionType();

        WebSeries webSeries = new WebSeries(name, ageLimit, rating, subscriptionType);
        webSeries = webSeriesRepository.save(webSeries);

        //associate WebSeries object with its ProductionHouse
        webSeries.setProductionHouse(productionHouse);
        List<WebSeries> webSeriesList = productionHouse.getWebSeriesList();
        webSeriesList.add(webSeries);
        productionHouse.setWebSeriesList(webSeriesList);

        //update rating of productionHouse;
        int oldNoOfWebSeries = productionHouse.getWebSeriesList().size() - 1;
        int updatedNoOfWebSeries = productionHouse.getWebSeriesList().size();
        double oldRating = productionHouse.getRatings();
        double updatedRating = ((oldRating * oldNoOfWebSeries) + webSeries.getRating()) / updatedNoOfWebSeries;

        productionHouse.setRatings(updatedRating);

        //save parent
        productionHouseRepository.save(productionHouse);

        int webSeriesId = webSeries.getId();

        return webSeriesId;
    }

}
