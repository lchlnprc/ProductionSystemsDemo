import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import type {
  Device,
  DeviceTestRun,
  TestRunDetail,
  TestRunSummary,
  TestRunLatestResponse
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
    })
  })
});

export const {
  useGetLatestTestRunsQuery,
  useGetDevicesQuery,
  useGetDeviceTestRunsQuery,
  useGetTestRunByIdQuery,
  useGetTestRunsQuery
} = api;
