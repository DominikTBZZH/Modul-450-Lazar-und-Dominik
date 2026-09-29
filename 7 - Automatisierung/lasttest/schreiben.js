import http from 'k6/http';
import { check } from 'k6';

export const options = {
  vus: 10,
  duration: '30s',
  thresholds: {
    http_req_failed: ['rate<0.01'],
  },
};

export default function () {
  const koerper = JSON.stringify({ name: 'Last', email: 'last@tbz.ch' });
  const antwort = http.post('http://localhost:8081/students', koerper, {
    headers: { 'Content-Type': 'application/json' },
  });
  check(antwort, { 'Status 200': (r) => r.status === 200 });
}
