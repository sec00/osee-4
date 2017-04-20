app.value("Schema",
{
  "type": "object",
  "properties": {
    "aircraftSystem": {
      "type": "string",
      "enum": [
        "Recorders",
        "Other"
      ]
    },
    "ship": {
      "type": "string",
      "enum": [
        "25",
        "26"
      ]
    },
    "testNumber": {
      "type": "string"
    },
    "testDate": {
      "type": "string",
      "format": "date"
    },
    "flightNumber": {
      "type": "string"
    },
    "build": {
      "type": "string",
      "enum": [
        "1.0",
        "FTB"
      ]
    },
    "swVersion": {
      "type": "string"
    },
    "originator": {
      "type": "string"
    },
    "pilotPOC": {
      "type": "string",
      "enum": [
        "Joe Smith",
        "Jane Smith"
      ]
    },
    "ftePOC": {
      "type": "string",
      "enum": [
        "Joe Smith",
        "Jane Smith"
      ]
    },
    "msaProgram": {
      "type": "string"
    },
    "priority": {
      "type": "string",
      "enum": [
        "1",
        "2",
        "3"
      ]
    },
    "status": {
      "type": "string",
      "enum": [
        "Open",
        "Close",
        "In Work"
      ]
    },
    "ipt": {
      "type": "string",
      "enum": [
        "WPN",
        "NAV",
        "COMM"
      ]
    },
    "subsystem": {
      "type": "string",
      "enum": [
        "MSM",
        "NAV",
        "COMM"
      ]
    },
    "id": {
      "type": "string"
    },
    "msaIssueId": {
      "type": "string"
    },
    "title": {
      "type": "string"
    },
    "originatorDate": {
      "type": "string",
      "format": "date"
    },
    "program": {
      "type": "string"
    },
    "description": {
      "type": "string"
    }
  }
}
);
