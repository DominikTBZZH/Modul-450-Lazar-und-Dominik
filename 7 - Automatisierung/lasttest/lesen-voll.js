import http from 'k6/http';
import { check } from 'k6';

export const options = {
  vus: 3,
  duration: '10s',
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<500'],
  },
};

export default function () {
  const antwort = http.get('http://localhost:8081/students');
  check(antwort, { 'Status 200': (r) => r.status === 200 });
}
