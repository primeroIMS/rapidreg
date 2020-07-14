package org.unicef.rapidreg;


import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
import org.unicef.rapidreg.model.Case;


@Database(name = PrimeroDatabaseConfiguration.NAME, version = PrimeroDatabaseConfiguration.VERSION)
public class PrimeroDatabaseConfiguration {
    public static final String NAME = "primero";
    public static final int VERSION = 2;


    @Migration(version = 2, priority = 2, database = PrimeroDatabaseConfiguration.class)
    public static class Migration20 extends AlterTableMigration<Case> {

        public Migration20(Class<Case> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.INTEGER, "last_note_alert_date");
        }
    }

}
