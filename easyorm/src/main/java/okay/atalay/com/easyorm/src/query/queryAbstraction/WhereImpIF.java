package okay.atalay.com.easyorm.src.query.queryAbstraction;

import java.util.List;

import okay.atalay.com.easyorm.src.query.clauses.Where;

/**
 * Created by 1 on 23.03.2018.
 */

public interface WhereImpIF {
    void addWhere(Where where);

    List<Where> getWhereClauses();
}
