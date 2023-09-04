package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        int id = userRepository.save(user).getId();
        return id;
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository

        List<WebSeries> allWebSeries = webSeriesRepository.findAll();
        User user = userRepository.findById(userId).get();

        int availableToWatch = 0;

        for(WebSeries webSeries : allWebSeries) {

            SubscriptionType userSubscription = user.getSubscription().getSubscriptionType();
            SubscriptionType webSeriesSubscription = webSeries.getSubscriptionType();

            if(getSubscriptionRank(userSubscription) >= getSubscriptionRank(webSeriesSubscription)) {

                if(user.getAge() >= webSeries.getAgeLimit()) {
                    availableToWatch++;
                }
            }
        }

        return availableToWatch;
    }
    private int getSubscriptionRank(SubscriptionType subscriptionType) {
        if(subscriptionType.equals(SubscriptionType.BASIC))
            return 1;
        if(subscriptionType.equals(SubscriptionType.PRO))
            return 2;
        else
            return 3;
    }


}
