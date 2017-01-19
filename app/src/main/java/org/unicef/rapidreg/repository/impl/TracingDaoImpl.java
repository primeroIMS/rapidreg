package org.unicef.rapidreg.repository.impl;

import com.raizlabs.android.dbflow.list.FlowQueryList;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.repository.TracingDao;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.model.Tracing_Table;

import java.util.ArrayList;
import java.util.List;

public class TracingDaoImpl implements TracingDao {

    @Override
    public Tracing save(Tracing tracing) {
        tracing.save();

        return tracing;
    }

    @Override
    public Tracing update(Tracing tracing) {
        tracing.update();

        return tracing;
    }

    @Override
    public Tracing getTracingByUniqueId(String uniqueId) {
        return SQLite.select().from(Tracing.class)
                .where(Tracing_Table.unique_id.eq(uniqueId))
                .querySingle();
    }

    @Override
    public List<Tracing> getAllTracingsOrderByDate(boolean isASC, String ownedBy) {
        return isASC ? getTracingsByDateASC(ownedBy) : getTracingsByDateDES(ownedBy);
    }

    @Override
    public List<Tracing> getAllTracingsByConditionGroup(ConditionGroup conditionGroup) {
        return SQLite.select().from(Tracing.class)
                .where(conditionGroup)
                .orderBy(Tracing_Table.registration_date, false)
                .queryList();
    }

    @Override
    public Tracing getTracingById(long tracingId) {
        return SQLite.select().from(Tracing.class).where(Tracing_Table.id.eq(tracingId)).querySingle();
    }

    @Override
    public Tracing getByInternalId(String id) {
        return SQLite.select().from(Tracing.class).where(Tracing_Table._id.eq(id)).querySingle();
    }

    @Override
    public List<Long> getAllIds(String ownedBy) {
        List<Long> result = new ArrayList<>();
        FlowQueryList<Tracing> cases = SQLite
                .select()
                .from(Tracing.class)
                .where(Tracing_Table.owned_by.eq(ownedBy))
                .flowQueryList();
        for (Tracing tracing : cases) {
            result.add(tracing.getId());
        }
        return result;
    }

    private List<Tracing> getTracingsByDateASC(String ownedBy) {
        return SQLite
                .select()
                .from(Tracing.class)
                .where(Tracing_Table.owned_by.eq(ownedBy))
                .orderBy(Tracing_Table.registration_date, true)
                .queryList();
    }

    private List<Tracing> getTracingsByDateDES(String ownedBy) {
        return SQLite
                .select()
                .from(Tracing.class)
                .where(Tracing_Table.owned_by.eq(ownedBy))
                .orderBy(Tracing_Table.registration_date, false)
                .queryList();
    }
}