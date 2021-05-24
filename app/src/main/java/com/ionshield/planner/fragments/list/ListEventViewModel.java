package com.ionshield.planner.fragments.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.ionshield.planner.database.PlannerRepository;
import com.ionshield.planner.database.entities.compound.EventAndItem;

import java.util.List;
import java.util.Objects;

public class ListEventViewModel extends ViewModel {
    private final MutableLiveData<ListEventViewModel.QueryFilter> filter = new MutableLiveData<>();
    private final MutableLiveData<Long> planId = new MutableLiveData<>();
    private final MutableLiveData<Mode> mode = new MutableLiveData<>();
    private final MutableLiveData<String> search = new MutableLiveData<>();
    private final LiveData<List<EventAndItem>> data;


    public ListEventViewModel() {
        data = Transformations.switchMap(filter, filter -> {
            if (filter.search == null || filter.search.isEmpty()) {
                if (filter.planId == null || filter.planId == 0) {
                    return Transformations.distinctUntilChanged(PlannerRepository.getInstance().getEventDao().liveEventsAndItems());
                }
                else {
                    return Transformations.distinctUntilChanged(PlannerRepository.getInstance().getEventDao().liveEventsAndItemsByPlanId(filter.planId));
                }
            }
            else {
                if (filter.planId == null || filter.planId == 0) {
                    return Transformations.distinctUntilChanged(PlannerRepository.getInstance().getEventDao().liveEventsAndItems("%" + filter.search + "%"));
                }
                else {
                    return Transformations.distinctUntilChanged(PlannerRepository.getInstance().getEventDao().liveEventsAndItemsByPlanId("%" + filter.search + "%", filter.planId));
                }
            }
        });
        setSearch("");
    }

    public LiveData<List<EventAndItem>> getData() {
        return data;
    }

    public void setMode(Mode mode) {
        this.mode.setValue(mode);
    }

    public LiveData<Mode> getMode() {
        return mode;
    }

    public void setSearch(String search) {
        if (!search.equals(this.search.getValue())) {
            this.search.setValue(search);
        }
        if (filter.getValue() == null) {
            filter.setValue(new ListEventViewModel.QueryFilter());
        }
        filter.getValue().search = search;
        filter.setValue(filter.getValue());
    }

    public LiveData<String> getSearch() {
        return search;
    }

    public void setPlanId(Long planId) {
        if (!planId.equals(this.planId.getValue())) {
            this.planId.setValue(planId);
        }
        if (filter.getValue() == null) {
            filter.setValue(new ListEventViewModel.QueryFilter());
        }
        filter.getValue().planId = planId;
        filter.setValue(filter.getValue());
    }

    public LiveData<Long> getPlanId() {
        return planId;
    }

    public static class QueryFilter {
        public Long planId;
        public String search;

        public QueryFilter() {
        }

        public QueryFilter(Long planId, String search) {
            this.planId = planId;
            this.search = search;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ListEventViewModel.QueryFilter that = (ListEventViewModel.QueryFilter) o;
            return Objects.equals(planId, that.planId) &&
                    Objects.equals(search, that.search);
        }

        @Override
        public int hashCode() {
            return Objects.hash(planId, search);
        }
    }
}
