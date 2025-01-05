import React, { useEffect, useState } from "react";
import {
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import "./dashboard.css";

const COLORS = ["#0088FE", "#00C49F", "#FFBB28", "#FF8042", "#845EC2", "#FF6F91", "#98D8A3"];

const Dashboard = () => {
  const [airportData, setAirportData] = useState({});
  const [avgAltitudeData, setAvgAltitudeData] = useState([]);
  const [avgLongitudeData, setAvgLongitudeData] = useState([]);

  useEffect(() => {
    const sock = new SockJS("http://localhost:3001/stomp");
    const stompClient = new Client({
      webSocketFactory: () => sock,
      onConnect: () => {
        console.log("Connected to WebSocket");

        // Subscribe to airport data updates
        stompClient.subscribe("/topic/airport-data", (message) => {
          const data = JSON.parse(message.body);
          const updatedData = { ...airportData };
          data.forEach((airport) => {
            updatedData[airport.country] = (updatedData[airport.country] || 0) + 1;
          });
          setAirportData(updatedData);
        });

        // Subscribe to average altitude data
        stompClient.subscribe("/topic/avg-altitude-data", (message) => {
          const data = JSON.parse(message.body);
          const formattedData = data.map((item) => ({
            name: item.country,
            value: item.average,
          }));
          setAvgAltitudeData(formattedData);
        });

        // Subscribe to average longitude data
        stompClient.subscribe("/topic/avg-longitude-data", (message) => {
          const data = JSON.parse(message.body);
          const formattedData = data.map((item) => ({
            name: item.country,
            value: item.average,
          }));
          console.log("Formatted Longitude Data:", formattedData);
          setAvgLongitudeData(formattedData);
        });
      },
      onStompError: (frame) => {
        console.error("WebSocket error:", frame);
      },
      onWebSocketClose: () => {
        console.error("WebSocket connection closed.");
      },
    });

    stompClient.activate();

    return () => {
      stompClient.deactivate();
    };
  }, [airportData]); // Include dependencies to avoid stale state

  const barChartData = Object.entries(airportData).map(([country, count]) => ({
    name: country,
    count,
  }));

  return (
    <div className="dashboard-container">
      <h1>Airport Dashboard</h1>
      <div className="chart-row">
        {/* Bar Chart */}
        <div className="chart-container">
          <h2>Number of Airports by Country</h2>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={barChartData}>
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="count" fill="#8884d8" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Pie Chart: Average Altitudes */}
        <div className="chart-container">
          <h2>Average Altitudes</h2>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={avgAltitudeData}
                dataKey={(entry) => Math.abs(entry.value)}
                nameKey="name"
                outerRadius={100}
                label={(entry) => `${entry.name}: ${entry.value.toFixed(2)}`}
              >
                {avgAltitudeData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      <div className="chart-row">
        {/* Pie Chart: Average Longitudes */}
        <div className="chart-container">
          <h2>Average Longitudes</h2>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={avgLongitudeData}
                dataKey={(entry) => Math.abs(entry.value)}
                nameKey="name"
                outerRadius={100}
                label={(entry) => `${entry.name}: ${entry.value.toFixed(2)}`}
              >
                {avgLongitudeData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
