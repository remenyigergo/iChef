import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.Cursor
import android.content.ContentValues
import android.database.sqlite.SQLiteOpenHelper

class AddParentChildDataManagerHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "favorite_parents.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "favorite_parents"
        const val COLUMN_NAME = "name"
        const val COLUMN_COUNTER = "counter"
    }

    // SQL to create the table
    private val CREATE_TABLE_SQL = """
        CREATE TABLE $TABLE_NAME (
            $COLUMN_NAME TEXT PRIMARY KEY,
            $COLUMN_COUNTER INTEGER DEFAULT 0
        );
    """

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_SQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Here we can handle database version upgrades if needed
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}
