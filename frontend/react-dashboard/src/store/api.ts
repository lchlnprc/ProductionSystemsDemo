import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import type {
  Device,
  DeviceTestRun,
  TestRunDetail,
  TestRunSummary,
  TestRunLatestResponse,
  TestExecutionResponse
} from "../types";

const baseUrl = import.meta.env.VITE_API_BASE_URL ?? "/api";

export const api = createApi({
  reducerPath: "api",
  baseQuery: fetchBaseQuery({ baseUrl }),
  endpoints: (builder) => ({
    getLatestTestRuns: builder.query<TestRunLatestResponse, void>({
      query: () => "/test-runs/latest"
    }),
    getDevices: builder.query<Device[], void>({
      query: () => "/devices"
    }),
    getDeviceTestRuns: builder.query<DeviceTestRun[], string>({
      query: (id) => `/devices/${id}/test-runs`
    }),
    getTestRunById: builder.query<TestRunDetail, string>({
      query: (id) => `/test-runs/${id}`
    }),
    getTestRuns: builder.query<TestRunSummary[], void>({
      query: () => "/test-runs"
    }),
    runTest: builder.mutation<TestExecutionResponse, { deviceId: string; requestedBy?: string; notes?: string }>({
      query: ({ deviceId, ...body }) => ({
        url: `/devices/${deviceId}/run-test`,
        method: "POST",
        body
      })
    }),
    getTestExecution: builder.query<TestExecutionResponse, string>({
      query: (id) => `/test-executions/${id}`
    })
  })
});

export const {
  useGetLatestTestRunsQuery,
  useGetDevicesQuery,
  useGetDeviceTestRunsQuery,
  useGetTestRunByIdQuery,
  useGetTestRunsQuery,
  useRunTestMutation,
  useGetTestExecutionQuery
} = api;
