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
        Commission.class,
        PayosPayment.class
}, version = 5, exportSchema = true)
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
    public abstract PayosPaymentDao payosPaymentDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "gallery_cart_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(roomCallback)
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

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

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `payos_payment` (" +
                    "`id` TEXT PRIMARY KEY NOT NULL, " +
                    "`cartId` INTEGER NOT NULL, " +
                    "`orderCode` INTEGER NOT NULL, " +
                    "`amount` INTEGER NOT NULL, " +
                    "`amountPaid` INTEGER NOT NULL, " +
                    "`amountRemaining` INTEGER NOT NULL, " +
                    "`status` TEXT NOT NULL, " +
                    "`createdAt` INTEGER, " +
                    "`canceledAt` INTEGER, " +
                    "`cancellationReason` TEXT, " +
                    "`transactionsJson` TEXT, " +
                    "FOREIGN KEY(`cartId`) REFERENCES `cart`(`id`) ON DELETE CASCADE)");

            database.execSQL("CREATE UNIQUE INDEX `index_payos_payment_cartId` ON `payos_payment` (`cartId`)");
            database.execSQL("CREATE INDEX `index_payos_payment_orderCode` ON `payos_payment` (`orderCode`)");

            database.execSQL("CREATE TABLE `cart_new` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`userId` INTEGER NOT NULL, " +
                    "`totalPrice` REAL NOT NULL, " +
                    "`purchaseDate` INTEGER, " +
                    "`createdDate` INTEGER, " +
                    "`isActive` INTEGER NOT NULL, " +
                    "FOREIGN KEY(`userId`) REFERENCES `user`(`id`) ON DELETE CASCADE)");

            database.execSQL("INSERT INTO `cart_new` (`id`, `userId`, `totalPrice`, `purchaseDate`, `createdDate`, `isActive`) " +
                    "SELECT `id`, `userId`, `totalPrice`, `purchaseDate`, " +
                    "COALESCE(`purchaseDate`, strftime('%s', 'now') * 1000), " +
                    "CASE WHEN `purchaseDate` IS NULL THEN 1 ELSE 0 END " +
                    "FROM `cart`");

            database.execSQL("DROP TABLE `cart`");

            database.execSQL("ALTER TABLE `cart_new` RENAME TO `cart`");

            database.execSQL("CREATE INDEX `index_cart_userId` ON `cart` (`userId`)");
        }
    };

    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE IF EXISTS payos_payment");

            database.execSQL("CREATE TABLE `payos_payment` (" +
                    "`id` TEXT NOT NULL PRIMARY KEY, " +
                    "`cartId` INTEGER NOT NULL, " +
                    "`orderCode` INTEGER NOT NULL, " +
                    "`amount` INTEGER NOT NULL, " +
                    "`amountPaid` INTEGER NOT NULL, " +
                    "`amountRemaining` INTEGER NOT NULL, " +
                    "`status` TEXT, " +
                    "`createdAt` INTEGER, " +
                    "`canceledAt` INTEGER, " +
                    "`cancellationReason` TEXT, " +
                    "`transactionsJson` TEXT, " +
                    "FOREIGN KEY(`cartId`) REFERENCES `cart`(`id`) ON DELETE CASCADE)");

            // Recreate indices
            database.execSQL("CREATE UNIQUE INDEX `index_payos_payment_cartId` ON `payos_payment` (`cartId`)");
            database.execSQL("CREATE INDEX `index_payos_payment_orderCode` ON `payos_payment` (`orderCode`)");
        }
    };

}
