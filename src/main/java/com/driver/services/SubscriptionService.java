package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay

        // get userId, subsciptionType, noOfScreens, totalAmountPaid, startDate
        int userId = subscriptionEntryDto.getUserId();
        User user = userRepository.findById(userId).get();

        SubscriptionType subscriptionType = subscriptionEntryDto.getSubscriptionType();
        int noOfScreens = subscriptionEntryDto.getNoOfScreensRequired();

        int totalAmtPaid = 0;
        if(subscriptionType.equals(SubscriptionType.BASIC)) {
            totalAmtPaid = 500 + 200 * noOfScreens;
        } else if(subscriptionType.equals(SubscriptionType.PRO)) {
            totalAmtPaid = 800 + 250 * noOfScreens;
        } else {
            totalAmtPaid = 1000 + 350 * noOfScreens;
        }

        Date startDate = new Date();

        // create Subscription object
        Subscription subscription = new Subscription(subscriptionType, noOfScreens, startDate, totalAmtPaid);

        //attach user to subscription
        user.setSubscription(subscription);
        subscription.setUser(user);

        //save parent
        userRepository.save(user);

        return totalAmtPaid;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user = userRepository.findById(userId).get();
        Subscription userSubscription = user.getSubscription();
        SubscriptionType userSubscriptionType = user.getSubscription().getSubscriptionType();

        //ELITE case
        if(userSubscriptionType.equals(SubscriptionType.ELITE))
            throw new Exception("Already the best Subscription");

        /*get old subscription and the number of screens to find the old price
        get new price based on subscription type
        get difference in money
        set subscription type to new type
        set total amount to new amount
        set new subscriptionStartdate */

        int noOfScreens = userSubscription.getNoOfScreensSubscribed();
        int oldPrice  = userSubscription.getTotalAmountPaid();

        int newPrice = 0;
        if(userSubscriptionType.equals(SubscriptionType.BASIC)) {
            //go to PRO
            newPrice = 800 + 250 * noOfScreens;
            userSubscription.setSubscriptionType(SubscriptionType.PRO);
        }
        else if(userSubscriptionType.equals(SubscriptionType.PRO)) {
            // go to ELITE
            newPrice = 1000 + 350 * noOfScreens;
            userSubscription.setSubscriptionType(SubscriptionType.ELITE);
        }

        int moneyToBePaid = newPrice - oldPrice;

        userSubscription.setTotalAmountPaid(newPrice);
        userSubscription.setStartSubscriptionDate(new Date());

        // update user and their subscription by bidirectional mapping and save parent
        user.setSubscription(userSubscription);
        userSubscription.setUser(user);

        userRepository.save(user);

        return moneyToBePaid;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> allSubscriptions = subscriptionRepository.findAll();
        int totalAmountEarned = 0;

        for(Subscription subscription : allSubscriptions) {
            totalAmountEarned += subscription.getTotalAmountPaid();
        }

        return totalAmountEarned;
    }

}
