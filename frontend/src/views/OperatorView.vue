<template>
  <main>
    <h1>Event Reservation MVP</h1>
    <p>ゲストでログインしてキーノート予約を行えます。</p>

    <button type="button" @click="loginAsGuest">ゲストでログイン</button>
    <p v-if="guestId">ログイン中: {{ guestId }}</p>
    <p v-if="errorMessage">{{ errorMessage }}</p>
    <p v-if="infoMessage">{{ infoMessage }}</p>

    <section>
      <h2>セッション管理（運営）</h2>
      <label>
        管理者トークン
        <input v-model="adminToken" type="password" placeholder="admin token" />
      </label>
      <button type="button" :disabled="!adminToken" @click="loadAdminSessions">
        管理一覧を取得
      </button>
      <button type="button" :disabled="!adminToken" @click="downloadReservationCsv">
        予約一覧CSVを出力
      </button>
      <button type="button" :disabled="!adminToken" @click="downloadSessionCheckInCsv">
        チェックインCSVを出力
      </button>

      <form @submit.prevent="createAdminSession">
        <h3>新規作成</h3>
        <label>
          タイトル
          <input v-model="createForm.title" type="text" required />
        </label>
        <label>
          開始時刻
          <input v-model="createForm.startTime" type="time" required />
        </label>
        <label>
          トラック
          <input v-model="createForm.track" type="text" required />
        </label>
        <label>
          定員
          <input v-model="createForm.capacity" type="number" min="1" required />
        </label>
        <button type="submit" :disabled="!adminToken">セッション作成</button>
      </form>

      <table v-if="adminSessions.length > 0">
        <thead>
          <tr>
            <th scope="col">ID</th>
            <th scope="col">開始時刻</th>
            <th scope="col">トラック</th>
            <th scope="col">タイトル</th>
            <th scope="col">定員</th>
            <th scope="col">予約数</th>
            <th scope="col">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="session in adminSessions" :key="session.sessionId">
            <td>{{ session.sessionId }}</td>
            <td>{{ session.startTime }}</td>
            <td>{{ session.track }}</td>
            <td>{{ session.title }}</td>
            <td>{{ session.capacity }}</td>
            <td>{{ session.reservedCount }}</td>
            <td>
              <button type="button" :disabled="!adminToken" @click="startEditSession(session)">
                編集
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <form v-if="editForm.sessionId" @submit.prevent="updateAdminSession">
        <h3>編集: {{ editForm.sessionId }}</h3>
        <label>
          タイトル
          <input v-model="editForm.title" type="text" required />
        </label>
        <label>
          開始時刻
          <input v-model="editForm.startTime" type="time" required />
        </label>
        <label>
          トラック
          <input v-model="editForm.track" type="text" required />
        </label>
        <label>
          定員
          <input v-model="editForm.capacity" type="number" min="1" required />
        </label>
        <button type="submit" :disabled="!adminToken">更新</button>
        <button type="button" @click="clearEditForm">キャンセル</button>
      </form>
    </section>

    <section>
      <h2>セッション一覧</h2>
      <button type="button" :disabled="!token" @click="loadSessions">セッション一覧を取得</button>
      <p v-if="token && sessions.length === 0">セッション一覧は未取得です。</p>
      <table v-else-if="sessions.length > 0">
        <thead>
          <tr>
            <th scope="col">開始時刻</th>
            <th scope="col">トラック</th>
            <th scope="col">セッション</th>
            <th scope="col">残席ステータス</th>
            <th scope="col">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="session in sessions" :key="session.sessionId">
            <td>{{ session.startTime }}</td>
            <td>{{ session.track }}</td>
            <td>{{ session.title }}</td>
            <td>{{ availabilityStatusLabel(session.availabilityStatus) }}</td>
            <td>
              <button
                type="button"
                :disabled="
                  !token ||
                  session.availabilityStatus === 'FULL' ||
                  isSessionReserved(session.sessionId)
                "
                @click="reserveSession(session.sessionId, session.title)"
              >
                {{ isSessionReserved(session.sessionId) ? '予約済み' : '予約する' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </section>

    <section>
      <h2>予約</h2>
      <button type="button" :disabled="!token" @click="loadReservations">予約一覧を取得</button>
      <button type="button" :disabled="!token" @click="reserveKeynote">キーノートを予約</button>
      <p v-if="registered">参加登録: 完了</p>
      <p v-else-if="token && registrationStatusLoaded">参加登録: 未完了</p>

      <ul>
        <li v-for="reservation in reservations" :key="reservation">
          {{ reservation }}
          <button type="button" :disabled="!token" @click="cancelReservation(reservation)">
            キャンセル
          </button>
        </li>
      </ul>
    </section>

    <section>
      <h2>マイページ</h2>
      <p v-if="!token">マイページはログイン中ユーザーのみ表示できます。</p>
      <template v-else>
        <button type="button" @click="loadMyPage">マイページを更新</button>
        <p v-if="myPageLoaded && myPageReservations.length === 0">予約はありません。</p>
        <ul v-else>
          <li v-for="reservation in myPageReservations" :key="`mypage-${reservation}`">
            {{ reservation }}
          </li>
        </ul>
        <img
          v-if="myPageQrCodePayload"
          :src="receptionQrCodeImageUrl"
          alt="受付用QRコード"
          width="180"
          height="180"
        />
        <p v-if="myPageQrCodePayload">受付用QRコードを表示中</p>
      </template>
    </section>

    <section>
      <h2>運営チェックイン</h2>
      <p v-if="!token">運営チェックインはログイン中ユーザーのみ実行できます。</p>
      <template v-else>
        <label for="checkin-qr-payload">QR payload</label>
        <textarea
          id="checkin-qr-payload"
          v-model="checkInQrCodePayload"
          rows="3"
          cols="60"
          placeholder="event-reservation://checkin?guestId=...&reservations=..."
        />
        <div>
          <button type="button" @click="checkInEvent">イベント受付をチェックイン</button>
        </div>
        <div>
          <label for="checkin-session-id">セッション受付</label>
          <select id="checkin-session-id" v-model="selectedCheckInSessionId">
            <option value="">選択してください</option>
            <option
              v-for="session in sessions"
              :key="`checkin-${session.sessionId}`"
              :value="session.sessionId"
            >
              {{ session.startTime }} {{ session.title }}
            </option>
          </select>
          <button
            type="button"
            :disabled="selectedCheckInSessionId.length === 0"
            @click="checkInSession"
          >
            セッションをチェックイン
          </button>
        </div>
        <button type="button" @click="loadCheckInHistory">チェックイン履歴を更新</button>
        <p v-if="checkInResultMessage">{{ checkInResultMessage }}</p>
        <p v-if="checkInHistoryLoaded && checkIns.length === 0">チェックイン履歴はありません。</p>
        <table v-else-if="checkIns.length > 0">
          <thead>
            <tr>
              <th scope="col">チェックイン種別</th>
              <th scope="col">ゲストID</th>
              <th scope="col">セッションID</th>
              <th scope="col">時刻</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="checkIn in checkIns"
              :key="`${checkIn.checkInType}-${checkIn.sessionId ?? 'event'}-${checkIn.guestId}`"
            >
              <td>{{ checkInTypeLabel(checkIn.checkInType) }}</td>
              <td>{{ checkIn.guestId }}</td>
              <td>{{ checkIn.sessionId ?? '-' }}</td>
              <td>{{ formatCheckInTime(checkIn.checkedInAt) }}</td>
            </tr>
          </tbody>
        </table>
      </template>
    </section>
  </main>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useReservationApp } from '../composables/useReservationApp';

const {
  token,
  adminToken,
  guestId,
  sessions,
  adminSessions,
  reservations,
  myPageReservations,
  myPageQrCodePayload,
  myPageLoaded,
  registered,
  registrationStatusLoaded,
  errorMessage,
  infoMessage,
  createForm,
  editForm,
  checkInQrCodePayload,
  selectedCheckInSessionId,
  checkIns,
  checkInHistoryLoaded,
  checkInResultMessage,
  receptionQrCodeImageUrl,
  availabilityStatusLabel,
  isSessionReserved,
  checkInTypeLabel,
  formatCheckInTime,
  clearEditForm,
  loginAsGuest,
  loadSessions,
  loadReservations,
  loadMyPage,
  loadAdminSessions,
  reserveKeynote,
  reserveSession,
  cancelReservation,
  downloadReservationCsv,
  downloadSessionCheckInCsv,
  createAdminSession,
  startEditSession,
  updateAdminSession,
  checkInEvent,
  checkInSession,
  loadCheckInHistory,
  loadOperatorBootstrap,
} = useReservationApp();

onMounted(() => {
  void loadOperatorBootstrap();
});
</script>
