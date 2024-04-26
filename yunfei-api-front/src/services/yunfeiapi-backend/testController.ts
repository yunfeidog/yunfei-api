// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** test GET /api/test/test */
export async function testUsingGet(options?: { [key: string]: any }) {
  return request<any>('/api/test/test', {
    method: 'GET',
    ...(options || {}),
  });
}
