package com.example.gallerycart.data;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.gallerycart.data.dao.*;
import com.example.gallerycart.data.entity.*;

@Database(entities = {
        User.class,
        Post.class,
        Comment.class,
        FavouritePost.class,
        Tag.class,
        PostTag.class,
        Cart.class,
        CartItem.class,
        MomoPayment.class,
        Commission.class
}, version = 3, exportSchema = true)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract PostDao postDao();
    public abstract CommentDao commentDao();
    public abstract FavouritePostDao favouritePostDao();
    public abstract TagDao tagDao();
    public abstract PostTagDao postTagDao();
    public abstract CartDao cartDao();
    public abstract CartItemDao cartItemDao();
    public abstract MomoPaymentDao momoPaymentDao();
    public abstract CommissionDao commissionDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "gallery_cart_database")
                            .addCallback(roomCallback)
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * RoomDatabase.Callback to enforce CHECK constraints on database creation
     * Note: Room doesn't support CHECK constraints directly in annotations,
     * so we can add them via raw SQL in onCreate if needed for DB-level enforcement.
     *
     * However, for better app-level control and clearer error messages,
     * validation should typically be done in the repository/service layer.
     */
    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // Example: Add CHECK constraints via raw SQL
            // These will enforce constraints at database level
            // Note: This runs AFTER Room creates tables, so we use ALTER TABLE or triggers

            // For likeCount >= 0
            db.execSQL("CREATE TRIGGER check_post_likeCount_insert " +
                    "BEFORE INSERT ON post " +
                    "WHEN NEW.likeCount < 0 " +
                    "BEGIN " +
                    "   SELECT RAISE(ABORT, 'likeCount must be >= 0'); " +
                    "END;");

            db.execSQL("CREATE TRIGGER check_post_likeCount_update " +
                    "BEFORE UPDATE ON post " +
                    "WHEN NEW.likeCount < 0 " +
                    "BEGIN " +
                    "   SELECT RAISE(ABORT, 'likeCount must be >= 0'); " +
                    "END;");

            // For saleCount >= 0
            db.execSQL("CREATE TRIGGER check_post_saleCount_insert " +
                    "BEFORE INSERT ON post " +
                    "WHEN NEW.saleCount < 0 " +
                    "BEGIN " +
                    "   SELECT RAISE(ABORT, 'saleCount must be >= 0'); " +
                    "END;");

            db.execSQL("CREATE TRIGGER check_post_saleCount_update " +
                    "BEFORE UPDATE ON post " +
                    "WHEN NEW.saleCount < 0 " +
                    "BEGIN " +
                    "   SELECT RAISE(ABORT, 'saleCount must be >= 0'); " +
                    "END;");

            // For price > 0
            db.execSQL("CREATE TRIGGER check_post_price_insert " +
                    "BEFORE INSERT ON post " +
                    "WHEN NEW.price <= 0 " +
                    "BEGIN " +
                    "   SELECT RAISE(ABORT, 'price must be > 0'); " +
                    "END;");

            db.execSQL("CREATE TRIGGER check_post_price_update " +
                    "BEFORE UPDATE ON post " +
                    "WHEN NEW.price <= 0 " +
                    "BEGIN " +
                    "   SELECT RAISE(ABORT, 'price must be > 0'); " +
                    "END;");

            // For MomoPayment amount > 0
            db.execSQL("CREATE TRIGGER check_momo_amount_insert " +
                    "BEFORE INSERT ON momo_payment " +
                    "WHEN NEW.amount <= 0 " +
                    "BEGIN " +
                    "   SELECT RAISE(ABORT, 'amount must be > 0'); " +
                    "END;");

            db.execSQL("CREATE TRIGGER check_momo_amount_update " +
                    "BEFORE UPDATE ON momo_payment " +
                    "WHEN NEW.amount <= 0 " +
                    "BEGIN " +
                    "   SELECT RAISE(ABORT, 'amount must be > 0'); " +
                    "END;");
        }
    };

    /**
     * Migration stub example for future schema changes
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE user ADD COLUMN isEmailConfirmed INTEGER NOT NULL DEFAULT 0");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `commissions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `artistId` TEXT, `clientId` TEXT, `description` TEXT, `price` REAL NOT NULL, `deadline` TEXT, `status` TEXT, `filePath` TEXT, `createdAt` INTEGER NOT NULL)");
        }
    };
}
