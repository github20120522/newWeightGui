package com.bolaihui.weight.gui.context;

import com.bolaihui.weight.gui.form.WeightForm;
import com.bolaihui.weight.gui.service.WeightService;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/10/26 0026.
 */
public class WeightContext {

    private WeightForm weightForm;

    private Set<String> zmdWeightYesSet = new HashSet<>();

    private Set<String> zmdWeightNoSet = new HashSet<>();

    private Set<String> zmdScanYesSet = new HashSet<>();

    private Set<String> zmdScanNoSet = new HashSet<>();

    private Set<String> cnWeightYesSet = new HashSet<>();

    private Set<String> cnWeightNoSet = new HashSet<>();

    private Set<String> cnCheckYesSet = new HashSet<>();

    private Set<String> cnCheckNoSet = new HashSet<>();

    private WeightService weightService = null;

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private WeightContext() {}

    private static WeightContext weightContext = new WeightContext();

    public static WeightContext getInstance() {
        return weightContext;
    }

    public WeightForm getWeightForm() {
        return weightForm;
    }

    public void setWeightForm(WeightForm weightForm) {
        this.weightForm = weightForm;
    }

    public Set<String> getZmdWeightYesSet() {
        return zmdWeightYesSet;
    }

    public Set<String> getZmdWeightNoSet() {
        return zmdWeightNoSet;
    }

    public Set<String> getZmdScanYesSet() {
        return zmdScanYesSet;
    }

    public Set<String> getZmdScanNoSet() {
        return zmdScanNoSet;
    }

    public Set<String> getCnWeightYesSet() {
        return cnWeightYesSet;
    }

    public Set<String> getCnWeightNoSet() {
        return cnWeightNoSet;
    }

    public Set<String> getCnCheckYesSet() {
        return cnCheckYesSet;
    }

    public Set<String> getCnCheckNoSet() {
        return cnCheckNoSet;
    }

    public WeightService getWeightService() {
        return weightService;
    }

    public void setWeightService(WeightService weightService) {
        this.weightService = weightService;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}
