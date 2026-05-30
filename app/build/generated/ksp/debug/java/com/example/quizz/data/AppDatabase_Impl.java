package com.example.quizz.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile QuizDao _quizDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `themes` (`id_theme` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nom_theme` TEXT NOT NULL, `nb_parties_jouees` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `questions` (`id_question` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `themeId` INTEGER NOT NULL, `texte` TEXT NOT NULL, FOREIGN KEY(`themeId`) REFERENCES `themes`(`id_theme`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `reponses` (`id_reponse` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `questionId` INTEGER NOT NULL, `texte` TEXT NOT NULL, `correcte` INTEGER NOT NULL, FOREIGN KEY(`questionId`) REFERENCES `questions`(`id_question`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `scores` (`id_score` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date_partie` TEXT NOT NULL, `points_obtenus` INTEGER NOT NULL, `total_questions` INTEGER NOT NULL, `themeId` INTEGER, FOREIGN KEY(`themeId`) REFERENCES `themes`(`id_theme`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'e15fb5f14c4ef55e8b9e1cdb7846a854')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `themes`");
        db.execSQL("DROP TABLE IF EXISTS `questions`");
        db.execSQL("DROP TABLE IF EXISTS `reponses`");
        db.execSQL("DROP TABLE IF EXISTS `scores`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsThemes = new HashMap<String, TableInfo.Column>(3);
        _columnsThemes.put("id_theme", new TableInfo.Column("id_theme", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsThemes.put("nom_theme", new TableInfo.Column("nom_theme", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsThemes.put("nb_parties_jouees", new TableInfo.Column("nb_parties_jouees", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysThemes = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesThemes = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoThemes = new TableInfo("themes", _columnsThemes, _foreignKeysThemes, _indicesThemes);
        final TableInfo _existingThemes = TableInfo.read(db, "themes");
        if (!_infoThemes.equals(_existingThemes)) {
          return new RoomOpenHelper.ValidationResult(false, "themes(com.exemple.quizz.Theme).\n"
                  + " Expected:\n" + _infoThemes + "\n"
                  + " Found:\n" + _existingThemes);
        }
        final HashMap<String, TableInfo.Column> _columnsQuestions = new HashMap<String, TableInfo.Column>(3);
        _columnsQuestions.put("id_question", new TableInfo.Column("id_question", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("themeId", new TableInfo.Column("themeId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("texte", new TableInfo.Column("texte", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysQuestions = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysQuestions.add(new TableInfo.ForeignKey("themes", "CASCADE", "NO ACTION", Arrays.asList("themeId"), Arrays.asList("id_theme")));
        final HashSet<TableInfo.Index> _indicesQuestions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoQuestions = new TableInfo("questions", _columnsQuestions, _foreignKeysQuestions, _indicesQuestions);
        final TableInfo _existingQuestions = TableInfo.read(db, "questions");
        if (!_infoQuestions.equals(_existingQuestions)) {
          return new RoomOpenHelper.ValidationResult(false, "questions(com.exemple.quizz.Question).\n"
                  + " Expected:\n" + _infoQuestions + "\n"
                  + " Found:\n" + _existingQuestions);
        }
        final HashMap<String, TableInfo.Column> _columnsReponses = new HashMap<String, TableInfo.Column>(4);
        _columnsReponses.put("id_reponse", new TableInfo.Column("id_reponse", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReponses.put("questionId", new TableInfo.Column("questionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReponses.put("texte", new TableInfo.Column("texte", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReponses.put("correcte", new TableInfo.Column("correcte", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReponses = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysReponses.add(new TableInfo.ForeignKey("questions", "CASCADE", "NO ACTION", Arrays.asList("questionId"), Arrays.asList("id_question")));
        final HashSet<TableInfo.Index> _indicesReponses = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoReponses = new TableInfo("reponses", _columnsReponses, _foreignKeysReponses, _indicesReponses);
        final TableInfo _existingReponses = TableInfo.read(db, "reponses");
        if (!_infoReponses.equals(_existingReponses)) {
          return new RoomOpenHelper.ValidationResult(false, "reponses(com.exemple.quizz.Reponse).\n"
                  + " Expected:\n" + _infoReponses + "\n"
                  + " Found:\n" + _existingReponses);
        }
        final HashMap<String, TableInfo.Column> _columnsScores = new HashMap<String, TableInfo.Column>(5);
        _columnsScores.put("id_score", new TableInfo.Column("id_score", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScores.put("date_partie", new TableInfo.Column("date_partie", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScores.put("points_obtenus", new TableInfo.Column("points_obtenus", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScores.put("total_questions", new TableInfo.Column("total_questions", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScores.put("themeId", new TableInfo.Column("themeId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysScores = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysScores.add(new TableInfo.ForeignKey("themes", "SET NULL", "NO ACTION", Arrays.asList("themeId"), Arrays.asList("id_theme")));
        final HashSet<TableInfo.Index> _indicesScores = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoScores = new TableInfo("scores", _columnsScores, _foreignKeysScores, _indicesScores);
        final TableInfo _existingScores = TableInfo.read(db, "scores");
        if (!_infoScores.equals(_existingScores)) {
          return new RoomOpenHelper.ValidationResult(false, "scores(com.exemple.quizz.Score).\n"
                  + " Expected:\n" + _infoScores + "\n"
                  + " Found:\n" + _existingScores);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "e15fb5f14c4ef55e8b9e1cdb7846a854", "db9d880214ab37790a6125f52254ef8b");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "themes","questions","reponses","scores");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `themes`");
      _db.execSQL("DELETE FROM `questions`");
      _db.execSQL("DELETE FROM `reponses`");
      _db.execSQL("DELETE FROM `scores`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(QuizDao.class, QuizDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public QuizDao quizDao() {
    if (_quizDao != null) {
      return _quizDao;
    } else {
      synchronized(this) {
        if(_quizDao == null) {
          _quizDao = new QuizDao_Impl(this);
        }
        return _quizDao;
      }
    }
  }
}
