angular.module('MiHexample').value("UISchema", 

{
  "type": "VerticalLayout",
  "elements": [
    {
      "type": "HorizontalLayout",
      "elements": [
        {
          "type": "Control",
          "label": "Subject",
          "scope": {
            "$ref": "#/properties/subject"
          },
          "readOnly": false
        },
        {
          "type": "Control",
          "label": "Discrepancy",
          "scope": {
            "$ref": "#/properties/discrepancy"
          },
          "readOnly": false
        }
      ]
    },
    {
      "type": "HorizontalLayout",
      "elements": [
        {
          "type": "Control",
          "label": "Status",
          "scope": {
            "$ref": "#/properties/status"
          },
          "readOnly": false
        },
        {
          "type": "Control",
          "label": "Type",
          "scope": {
            "$ref": "#/properties/type"
          },
          "readOnly": false
        }
      ]
    },
    {
      "type": "HorizontalLayout",
      "elements": [
        {
          "type": "Control",
          "label": "Responsible Ipt",
          "scope": {
            "$ref": "#/properties/responsibleIpt"
          },
          "readOnly": false
        },
        {
          "type": "Control",
          "label": "Color Team",
          "scope": {
            "$ref": "#/properties/colorTeam"
          },
          "readOnly": false
        }
      ]
    }
  ]
}

);
