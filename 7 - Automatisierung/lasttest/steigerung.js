import http from 'k6/http';
import { check } from 'k6';

export const options = {
  stages: [
    { duration: '20s', target: 50 },
    { duration: '20s', target: 150 },
    { duration: '20s', target: 300 },
    { duration: '10s', target: 0 },
  ],
};

export default function () {
  const antwort = http.get('http://localhost:8081/students');
  check(antwort, { 'Status 200': (r) => r.status === 200 });
}
