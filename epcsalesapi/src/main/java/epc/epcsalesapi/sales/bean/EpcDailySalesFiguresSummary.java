/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;

/**
 *
 * @author DannyChan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EpcDailySalesFiguresSummary {
    private ArrayList<EpcDailySalesFiguresItem> offers;
    private ArrayList<EpcDailySalesFiguresItem> activationTypes;
    private ArrayList<EpcDailySalesFiguresItem> plans;
    private ArrayList<EpcDailySalesFiguresItem> vas;
    private ArrayList<EpcDailySalesFiguresItem> handsets;
    private ArrayList<EpcDailySalesFiguresItem> accessories;

    /**
     * @return the offers
     */
    public ArrayList<EpcDailySalesFiguresItem> getOffers() {
        return offers;
    }

    /**
     * @param offers the offers to set
     */
    public void setOffers(ArrayList<EpcDailySalesFiguresItem> offers) {
        this.offers = offers;
    }

    /**
     * @return the activationTypes
     */
    public ArrayList<EpcDailySalesFiguresItem> getActivationTypes() {
        return activationTypes;
    }

    /**
     * @param activationTypes the activationTypes to set
     */
    public void setActivationTypes(ArrayList<EpcDailySalesFiguresItem> activationTypes) {
        this.activationTypes = activationTypes;
    }

    /**
     * @return the plans
     */
    public ArrayList<EpcDailySalesFiguresItem> getPlans() {
        return plans;
    }

    /**
     * @param plans the plans to set
     */
    public void setPlans(ArrayList<EpcDailySalesFiguresItem> plans) {
        this.plans = plans;
    }

    /**
     * @return the vas
     */
    public ArrayList<EpcDailySalesFiguresItem> getVas() {
        return vas;
    }

    /**
     * @param vas the vas to set
     */
    public void setVas(ArrayList<EpcDailySalesFiguresItem> vas) {
        this.vas = vas;
    }

    /**
     * @return the handsets
     */
    public ArrayList<EpcDailySalesFiguresItem> getHandsets() {
        return handsets;
    }

    /**
     * @param handsets the handsets to set
     */
    public void setHandsets(ArrayList<EpcDailySalesFiguresItem> handsets) {
        this.handsets = handsets;
    }

    /**
     * @return the accessories
     */
    public ArrayList<EpcDailySalesFiguresItem> getAccessories() {
        return accessories;
    }

    /**
     * @param accessories the accessories to set
     */
    public void setAccessories(ArrayList<EpcDailySalesFiguresItem> accessories) {
        this.accessories = accessories;
    }


}
