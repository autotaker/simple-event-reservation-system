<template>
  <main>
    <h1>Event Reservation MVP</h1>
    <p>ゲストでログインしてキーノート予約を行えます。</p>

    <button type="button" @click="loginAsGuest">ゲストでログイン</button>
    <p v-if="guestId">ログイン中: {{ guestId }}</p>
    <p v-if="errorMessage">{{ errorMessage }}</p>
    <p v-if="infoMessage">{{ infoMessage }}</p>

    <button type="button" :disabled="!token" @click="loadReservations">予約一覧を取得</button>
    <button type="button" :disabled="!token" @click="reserveKeynote">キーノートを予約</button>
    <p v-if="registered">参加登録: 完了</p>
    <p v-else-if="token && registrationStatusLoaded">参加登録: 未完了</p>

    <ul>
      <li v-for="reservation in reservations" :key="reservation">{{ reservation }}</li>
    </ul>
  </main>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';

type GuestLoginResponse = {
  accessToken: string;
  guestId: string;
  tokenType: string;
};

type ReservationResponse = {
  guestId: string;
  reservations: string[];
  registered?: boolean;
};

const API_BASE_URL = 'http://127.0.0.1:8080';
const token = ref<string | null>(globalThis.localStorage.getItem('guestAccessToken'));
const guestId = ref<string>(globalThis.localStorage.getItem('guestId') ?? '');
const reservations = ref<string[]>([]);
const registered = ref<boolean>(false);
const registrationStatusLoaded = ref<boolean>(false);
const errorMessage = ref<string>('');
const infoMessage = ref<string>('');

const loginAsGuest = async (): Promise<void> => {
  errorMessage.value = '';
  infoMessage.value = '';
  const response = await globalThis.fetch(`${API_BASE_URL}/api/auth/guest`, {
    method: 'POST',
  });

  if (!response.ok) {
    errorMessage.value = 'ゲストログインに失敗しました。';
    return;
  }

  const data = (await response.json()) as GuestLoginResponse;
  token.value = data.accessToken;
  guestId.value = data.guestId;
  reservations.value = [];
  registered.value = false;
  registrationStatusLoaded.value = false;
  globalThis.localStorage.setItem('guestAccessToken', data.accessToken);
  globalThis.localStorage.setItem('guestId', data.guestId);
};

const loadReservations = async (): Promise<void> => {
  if (!token.value) {
    return;
  }

  errorMessage.value = '';
  infoMessage.value = '';
  const response = await globalThis.fetch(`${API_BASE_URL}/api/reservations`, {
    headers: {
      Authorization: `Bearer ${token.value}`,
    },
  });

  if (!response.ok) {
    errorMessage.value = '予約取得に失敗しました。';
    return;
  }

  const data = (await response.json()) as ReservationResponse;
  reservations.value = data.reservations;
  registered.value = data.registered ?? data.reservations.includes('keynote');
  registrationStatusLoaded.value = true;
};

const reserveKeynote = async (): Promise<void> => {
  if (!token.value) {
    return;
  }

  errorMessage.value = '';
  infoMessage.value = '';
  const response = await globalThis.fetch(`${API_BASE_URL}/api/reservations/keynote`, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token.value}`,
    },
  });

  if (response.status === 409) {
    errorMessage.value = 'キーノートは満席です。';
    return;
  }

  if (!response.ok) {
    errorMessage.value = 'キーノート予約に失敗しました。';
    return;
  }

  const data = (await response.json()) as ReservationResponse;
  reservations.value = data.reservations;
  registered.value = data.registered ?? data.reservations.includes('keynote');
  registrationStatusLoaded.value = true;
  infoMessage.value = 'キーノートを予約しました。';
};

onMounted(() => {
  if (token.value) {
    void loadReservations();
  }
});
</script>
