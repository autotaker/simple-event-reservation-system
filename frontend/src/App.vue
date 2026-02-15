<template>
  <main>
    <h1>Event Reservation MVP</h1>
    <p>ゲストでログインして予約APIの接続を確認できます。</p>

    <button type="button" @click="loginAsGuest">ゲストでログイン</button>
    <p v-if="guestId">ログイン中: {{ guestId }}</p>
    <p v-if="errorMessage">{{ errorMessage }}</p>

    <button type="button" :disabled="!token" @click="loadReservations">予約一覧を取得</button>

    <ul>
      <li v-for="reservation in reservations" :key="reservation">{{ reservation }}</li>
    </ul>
  </main>
</template>

<script setup lang="ts">
import { ref } from 'vue';

type GuestLoginResponse = {
  accessToken: string;
  guestId: string;
  tokenType: string;
};

type ReservationResponse = {
  guestId: string;
  reservations: string[];
};

const token = ref<string | null>(globalThis.localStorage.getItem('guestAccessToken'));
const guestId = ref<string>(globalThis.localStorage.getItem('guestId') ?? '');
const reservations = ref<string[]>([]);
const errorMessage = ref<string>('');

const loginAsGuest = async (): Promise<void> => {
  errorMessage.value = '';
  const response = await globalThis.fetch('http://127.0.0.1:8080/api/auth/guest', {
    method: 'POST',
  });

  if (!response.ok) {
    errorMessage.value = 'ゲストログインに失敗しました。';
    return;
  }

  const data = (await response.json()) as GuestLoginResponse;
  token.value = data.accessToken;
  guestId.value = data.guestId;
  globalThis.localStorage.setItem('guestAccessToken', data.accessToken);
  globalThis.localStorage.setItem('guestId', data.guestId);
};

const loadReservations = async (): Promise<void> => {
  if (!token.value) {
    return;
  }

  errorMessage.value = '';
  const response = await globalThis.fetch('http://127.0.0.1:8080/api/reservations', {
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
};
</script>
