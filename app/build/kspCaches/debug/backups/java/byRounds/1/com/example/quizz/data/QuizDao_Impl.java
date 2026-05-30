package com.example.quizz.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.exemple.quizz.Question;
import com.exemple.quizz.Reponse;
import com.exemple.quizz.Score;
import com.exemple.quizz.ScoreAvecTheme;
import com.exemple.quizz.Theme;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class QuizDao_Impl implements QuizDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Score> __insertionAdapterOfScore;

  private final EntityInsertionAdapter<Theme> __insertionAdapterOfTheme;

  private final EntityInsertionAdapter<Question> __insertionAdapterOfQuestion;

  private final EntityInsertionAdapter<Reponse> __insertionAdapterOfReponse;

  private final EntityDeletionOrUpdateAdapter<Score> __deletionAdapterOfScore;

  private final SharedSQLiteStatement __preparedStmtOfConserverUniquementLes20DerniersScores;

  private final SharedSQLiteStatement __preparedStmtOfIncrementerCompteurTheme;

  public QuizDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfScore = new EntityInsertionAdapter<Score>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `scores` (`id_score`,`date_partie`,`points_obtenus`,`total_questions`,`themeId`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Score entity) {
        statement.bindLong(1, entity.getId_score());
        statement.bindString(2, entity.getDate_partie());
        statement.bindLong(3, entity.getPoints_obtenus());
        statement.bindLong(4, entity.getTotal_questions());
        if (entity.getThemeId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getThemeId());
        }
      }
    };
    this.__insertionAdapterOfTheme = new EntityInsertionAdapter<Theme>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `themes` (`id_theme`,`nom_theme`,`nb_parties_jouees`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Theme entity) {
        statement.bindLong(1, entity.getId_theme());
        statement.bindString(2, entity.getNom_theme());
        statement.bindLong(3, entity.getNb_parties_jouees());
      }
    };
    this.__insertionAdapterOfQuestion = new EntityInsertionAdapter<Question>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `questions` (`id_question`,`themeId`,`texte`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Question entity) {
        statement.bindLong(1, entity.getId_question());
        statement.bindLong(2, entity.getThemeId());
        statement.bindString(3, entity.getTexte());
      }
    };
    this.__insertionAdapterOfReponse = new EntityInsertionAdapter<Reponse>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `reponses` (`id_reponse`,`questionId`,`texte`,`correcte`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Reponse entity) {
        statement.bindLong(1, entity.getId_reponse());
        statement.bindLong(2, entity.getQuestionId());
        statement.bindString(3, entity.getTexte());
        final int _tmp = entity.getCorrecte() ? 1 : 0;
        statement.bindLong(4, _tmp);
      }
    };
    this.__deletionAdapterOfScore = new EntityDeletionOrUpdateAdapter<Score>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `scores` WHERE `id_score` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Score entity) {
        statement.bindLong(1, entity.getId_score());
      }
    };
    this.__preparedStmtOfConserverUniquementLes20DerniersScores = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM scores WHERE id_score NOT IN (SELECT id_score FROM scores ORDER BY id_score DESC LIMIT 20)";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementerCompteurTheme = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE themes SET nb_parties_jouees = nb_parties_jouees + 1 WHERE id_theme = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertScore(final Score score, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfScore.insert(score);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertTheme(final Theme theme, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTheme.insertAndReturnId(theme);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertQuestion(final Question question,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfQuestion.insertAndReturnId(question);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertReponses(final List<Reponse> reponses,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfReponse.insert(reponses);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteScore(final Score score, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfScore.handle(score);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object conserverUniquementLes20DerniersScores(
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfConserverUniquementLes20DerniersScores.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfConserverUniquementLes20DerniersScores.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementerCompteurTheme(final int idTheme,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementerCompteurTheme.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, idTheme);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfIncrementerCompteurTheme.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllThemes(final Continuation<? super List<Theme>> $completion) {
    final String _sql = "SELECT * FROM themes";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Theme>>() {
      @Override
      @NonNull
      public List<Theme> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIdTheme = CursorUtil.getColumnIndexOrThrow(_cursor, "id_theme");
          final int _cursorIndexOfNomTheme = CursorUtil.getColumnIndexOrThrow(_cursor, "nom_theme");
          final int _cursorIndexOfNbPartiesJouees = CursorUtil.getColumnIndexOrThrow(_cursor, "nb_parties_jouees");
          final List<Theme> _result = new ArrayList<Theme>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Theme _item;
            final int _tmpId_theme;
            _tmpId_theme = _cursor.getInt(_cursorIndexOfIdTheme);
            final String _tmpNom_theme;
            _tmpNom_theme = _cursor.getString(_cursorIndexOfNomTheme);
            final int _tmpNb_parties_jouees;
            _tmpNb_parties_jouees = _cursor.getInt(_cursorIndexOfNbPartiesJouees);
            _item = new Theme(_tmpId_theme,_tmpNom_theme,_tmpNb_parties_jouees);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getQuestionsByTheme(final int idTheme,
      final Continuation<? super List<QuestionComplete>> $completion) {
    final String _sql = "SELECT * FROM questions WHERE themeId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, idTheme);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, true, _cancellationSignal, new Callable<List<QuestionComplete>>() {
      @Override
      @NonNull
      public List<QuestionComplete> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfIdQuestion = CursorUtil.getColumnIndexOrThrow(_cursor, "id_question");
            final int _cursorIndexOfThemeId = CursorUtil.getColumnIndexOrThrow(_cursor, "themeId");
            final int _cursorIndexOfTexte = CursorUtil.getColumnIndexOrThrow(_cursor, "texte");
            final LongSparseArray<ArrayList<Reponse>> _collectionReponses = new LongSparseArray<ArrayList<Reponse>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfIdQuestion);
              if (!_collectionReponses.containsKey(_tmpKey)) {
                _collectionReponses.put(_tmpKey, new ArrayList<Reponse>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipreponsesAscomExempleQuizzReponse(_collectionReponses);
            final List<QuestionComplete> _result = new ArrayList<QuestionComplete>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final QuestionComplete _item;
              final Question _tmpQuestion;
              final int _tmpId_question;
              _tmpId_question = _cursor.getInt(_cursorIndexOfIdQuestion);
              final int _tmpThemeId;
              _tmpThemeId = _cursor.getInt(_cursorIndexOfThemeId);
              final String _tmpTexte;
              _tmpTexte = _cursor.getString(_cursorIndexOfTexte);
              _tmpQuestion = new Question(_tmpId_question,_tmpThemeId,_tmpTexte);
              final ArrayList<Reponse> _tmpReponsesCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfIdQuestion);
              _tmpReponsesCollection = _collectionReponses.get(_tmpKey_1);
              _item = new QuestionComplete(_tmpQuestion,_tmpReponsesCollection);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
            _statement.release();
          }
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllScores(final Continuation<? super List<ScoreAvecTheme>> $completion) {
    final String _sql = "\n"
            + "    SELECT s.id_score, s.date_partie, s.points_obtenus, s.total_questions, t.nom_theme \n"
            + "    FROM scores s \n"
            + "    LEFT JOIN themes t ON s.themeId = t.id_theme \n"
            + "    ORDER BY s.id_score DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ScoreAvecTheme>>() {
      @Override
      @NonNull
      public List<ScoreAvecTheme> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIdScore = 0;
          final int _cursorIndexOfDatePartie = 1;
          final int _cursorIndexOfPointsObtenus = 2;
          final int _cursorIndexOfTotalQuestions = 3;
          final int _cursorIndexOfNomTheme = 4;
          final List<ScoreAvecTheme> _result = new ArrayList<ScoreAvecTheme>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScoreAvecTheme _item;
            final int _tmpId_score;
            _tmpId_score = _cursor.getInt(_cursorIndexOfIdScore);
            final String _tmpDate_partie;
            _tmpDate_partie = _cursor.getString(_cursorIndexOfDatePartie);
            final int _tmpPoints_obtenus;
            _tmpPoints_obtenus = _cursor.getInt(_cursorIndexOfPointsObtenus);
            final int _tmpTotal_questions;
            _tmpTotal_questions = _cursor.getInt(_cursorIndexOfTotalQuestions);
            final String _tmpNom_theme;
            if (_cursor.isNull(_cursorIndexOfNomTheme)) {
              _tmpNom_theme = null;
            } else {
              _tmpNom_theme = _cursor.getString(_cursorIndexOfNomTheme);
            }
            _item = new ScoreAvecTheme(_tmpId_score,_tmpDate_partie,_tmpPoints_obtenus,_tmpTotal_questions,_tmpNom_theme);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipreponsesAscomExempleQuizzReponse(
      @NonNull final LongSparseArray<ArrayList<Reponse>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshipreponsesAscomExempleQuizzReponse(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id_reponse`,`questionId`,`texte`,`correcte` FROM `reponses` WHERE `questionId` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "questionId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfIdReponse = 0;
      final int _cursorIndexOfQuestionId = 1;
      final int _cursorIndexOfTexte = 2;
      final int _cursorIndexOfCorrecte = 3;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        final ArrayList<Reponse> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final Reponse _item_1;
          final int _tmpId_reponse;
          _tmpId_reponse = _cursor.getInt(_cursorIndexOfIdReponse);
          final int _tmpQuestionId;
          _tmpQuestionId = _cursor.getInt(_cursorIndexOfQuestionId);
          final String _tmpTexte;
          _tmpTexte = _cursor.getString(_cursorIndexOfTexte);
          final boolean _tmpCorrecte;
          final int _tmp;
          _tmp = _cursor.getInt(_cursorIndexOfCorrecte);
          _tmpCorrecte = _tmp != 0;
          _item_1 = new Reponse(_tmpId_reponse,_tmpQuestionId,_tmpTexte,_tmpCorrecte);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
