app.value("UISchema", 
{
  "type": "VerticalLayout",
  "elements": [
    {
      "type": "HorizontalLayout",
      "elements": [
        {
          "type": "Control",
          "label": "Title",
          "scope": {
            "$ref": "#/properties/title"
          },
          "options": {
            "useCustom": true
          },
          "readOnly": false
        },
        {
          "type": "HorizontalLayout",
          "elements": [
            {
              "type": "Control",
              "label": "Flight Test Issue Id",
              "scope": {
                "$ref": "#/properties/id"
              },
              "readOnly": true,
            },
            {
            "type": "Control",
            "label": "Linked MSA Issue ID",
            "scope": {
              "$ref": "#/properties/msaIssueId"
            },
            "readOnly": true,
            "rule": {
              "effect": "HIDE",
              "condition": {
                "scope": {
                  "$ref": "#/properties/title"
                },
                "expectedValue": "Hello"
              }
            }
          }
          ]
        },
        {
          "type": "Control",
          "label": "MSA Program",
          "scope": {
            "$ref": "#/properties/program"
          },
          "readOnly": true
        }
      ]
    },
    {
      "type": "HorizontalLayout",
      "elements": [
        {
          "type": "VerticalLayout",
          "elements": [
            {
              "type": "Control",
              "label": "Status",
              "scope": {
                "$ref": "#/properties/status"
              },
              "readOnly": true,
            },
            {
              "type": "Control",
              "label": "Originator",
              "scope": {
                "$ref": "#/properties/originator"
              },
              "readOnly": false
            },
            {
              "type": "Control",
              "label": "Originator Date",
              "scope": {
                "$ref": "#/properties/originatorDate"
              },
              "readOnly": false,
              "options": {
                "multi": true
              }
            }
          ]
        },
        {
          "type": "VerticalLayout",
          "elements": [
            {
              "type": "Control",
              "label": "Pilot POC",
              "scope": {
                "$ref": "#/properties/pilotPOC"
              },
              "readOnly": false,
            },
            {
              "type": "Control",
              "label": "FTE POC",
              "scope": {
                "$ref": "#/properties/ftePOC"
              },
              "readOnly": false,
            },
            {
              "type": "Control",
              "label": "IPT",
              "scope": {
                "$ref": "#/properties/ipt"
              },
              "readOnly": true,
            }
          ]          
        },
        {
          "type": "VerticalLayout",
          "elements": [
            {
              "type": "Control",
              "label": "Ship",
              "scope": {
                "$ref": "#/properties/ship"
              },
              "readOnly": false,
            },
            {
              "type": "Control",
              "label": "Test Number",
              "scope": {
                "$ref": "#/properties/testNumber"
              },
              "readOnly": false,
            },
            {
              "type": "Control",
              "label": "Flight Number",
              "scope": {
                "$ref": "#/properties/flightNumber"
              },
              "readOnly": false,
            },
            {
              "type": "Control",
              "label": "Test Date",
              "scope": {
                "$ref": "#/properties/testDate"
              },
              "readOnly": false,
            }
          ]          
        },
        {
          "type": "VerticalLayout",
          "elements": [
            {
              "type": "Control",
              "label": "Build",
              "scope": {
                "$ref": "#/properties/build"
              },
              "readOnly": false,
            },
            {
              "type": "Control",
              "label": "SW Version",
              "scope": {
                "$ref": "#/properties/swVersion"
              },
              "readOnly": false,
            },
            {
              "type": "Control",
              "label": "Aircraft System",
              "scope": {
                "$ref": "#/properties/aircraftSystem"
              },
              "readOnly": false
            },
            {
              "type": "Control",
              "label": "Subsystem",
              "scope": {
                "$ref": "#/properties/subsystem"
              },
              "readOnly": false
            }
          ]          
        }
        
      ]
    },
    {
      "type": "HorizontalLayout",
      "elements": [
        {
          "type": "Control",
          "label": "Description",
          "scope": {
            "$ref": "#/properties/description"
          },
          "readOnly": false,
          "options": {
            "multi": true
          }
        },
        {
          "type": "Control",
          "label": "Disposition",
          "scope": {
            "$ref": "#/properties/disposition"
          },
          "readOnly": false,
          "options": {
            "multi": true
          }
        }
      ]
    }
  ]
}
);
