curl -f -s --location --request POST "${ICE_BASE_URI}/opencds-decision-support-service/api/resources/evaluate" \
--header 'Content-Type: application/json' \
--header 'Accept: application/json' \
--data-raw '{
    "interactionId": {
        "scopingEntityId": "org.nyc.cir",
        "interactionId": "123456",
        "submissionTime": 1579330800000
    },
    "evaluationRequest": {
        "clientLanguage": "en",
        "clientTimeZoneOffset": "+0000",
        "kmEvaluationRequest": [{
                "kmId": {
                    "scopingEntityId": "org.nyc.cir",
                    "businessId": "ICE",
                    "version": "1.0.0"
                }
            }],
        "dataRequirementItemData": [{
                "driId": {
                    "containingEntityId": {
                        "scopingEntityId": "org.nyc.cir",
                        "businessId": "GZIPICEData",
                        "version": "1.0.0"
                    },
                    "itemId": "cdsPayload"
                },
                "data": {
                    "informationModelSSId": {
                        "scopingEntityId": "org.opencds.vmr",
                        "businessId": "VMR",
                        "version": "1.0"
                    },
                    "base64EncodedPayload": ["H4sICN5qJF4CA3NhbXBsZUNkc0lucHV0LnhtbADtnVlvGzkSgN/nVzT0sMg8UOZ9zDgZOI4nY2CcMWIni30KeBTtXkgtQS15nP31W634kiwfaSwWVrp9ShTJ5lEfWUVWs3d/uxyPiguY1eWkej1gQzoooIqTVFZnrwefTn8ndlDUc18lP5pU8HrwFerBb29+2q1q8UtM9WE1XcwLzKOqf6lq/nowmZ0NJ1PMItXDi/FseMG+0GEdz2HshxhWNvGH9RRimcvo53jVwU/F1dd1NuJ52dxPKJ9MOFnMN6ZUj6fE94M3y0S7cxhPR34Oh6mYTSbz1wM+ZHpoJR2yIWPCWjEUQ+MUvmlCBjtX6TDb/Uk1h8v5m5urN4EnX2vM8lMNs+MZZJjNIP3pq7OFP4MC+wGbHKrB8tW3mJsuqIfODYpU1liyrx/8GBMdVGejsj6/ufzO+vV3sU7L3rtTnJZ1W6adYmdCdSe3786Rr+e5zKF8MiV+QDGUoeRezrEIS0mWgjunFAauZ5hgPDmb+el5GevVj5Yfh3I2Pz8tx1Bc+NECW5I55wjVhNH1si3jn0GVYHbVVUdP9ZTaUMOdh0u0G0dlhZyMTubYiGOs3KYiTwJKz8USpo9QL0abIm2OuDned/ecxpdsU+t8fy+aq178Nh5Jbh7N9k6Ffp/ERX3VDVTxoXsaGkbFc3M/uMC2X0rFeXl2jtlRpgll+DMoRpO/VwKemeXnRrwejvqt+ydVhOn8qlrvDk8O9k4Ovrz7a//T0cGH04N3T9VxpW3toyXbeX7RdkscR2bTGcyXsa9Kd3jy5fDo6NOHg+8qlHuoUCsFekxat1GqXTup1r1U91L9YqVa0Jc8Vst1qZa9VP9fpXpDpE3KTL0Ijb0RYS+NUfep57PbTn1Ir3kszf8IG7dJOW2HDV3BRmmn2ePzwQPVew8VzPzoeDGbTuprY0FYafEqlA8eldbm6ynk1PMK9QQWN81inNVBSkVyYolIxYBYqoCoEAJEg7/5UcRXr7qPhb8eQeya7fMHTN8Wr/7hx9NfR/NfOS2+zupCkISt9PNTAo+dxB1/nL5nVH3Xr3RVMxodNiCiTXE1Ki3NCkYJ01ej0p2ABzH7fkHfejY47QIbzCcejSMyS0mk5EBCZplIj99ZZ1A8tmPDrLFxtBhP6x6CbYOAdwACAYYrFyWBYAWRVmriAf8EymmApJix7SaIpkFXIDg8/twjsG0IiA4goEOgHrwnKmmOCCRDfNQBZwTmEo0uJWrbIcDWEDiG2XxR12U/F2wdCLIDIGQTs1LOEEU1RTUIGPFGUAIKNSRtmI2atQOBr4Hwrpyez89hVvpir5qX88llWfVMbBkToguTgxfRJo5MmOTRSLDWEasUJ4gKBYcmRGjLhFhj4hQwyqIuyvF4UUFxNpqExaisilenh+97C3rr4FBdmDAgcmaYIjIm1JwMwuFBoBkdEAxvorMU2sEh142H98WHv056CraMAt2FdaQYtXJMBqIdZThFBIPWM1gSjWcuAw0yunYUqHUKqjxaQPUfT8rq3xDnPoygZ2LbmOiC2pSZyDlERXgMHmcGCsRRZUnmEUSQIWM7tGNifd/how8l1AgENtR4UcfFyM+Kb3g0fm49HVtGh+3CipPiAkwUhHEGRKpgiUWNkSgnnGAmMuNbzhjrHolv99/3CGwZArYLa03gY4QQmo1pjXY1F5oEg5qT1ZBVtoFGQ1shwNcXXT/7WRlhNPI9CNsGQhdsaK09l5F6QoVH6yGjkoTakSNaWM9tVIlG3g6Ee4uup8c7f5Shx2DbMNBdcMbgOQjOBckSUCVKghHHJSfSo9kglE5etltK4uvrrMcjf7bozeato8B0gAJmnZRBSOIZa5aSJE4GyQDBskbnpEClKLSjYH1Bda+an8/8ZY/BNmFgtOGsAxhQSxNEE4nOzhMpQiaWapwRjPbMgtHOtMOArk8GR0cfXwwCnFJHqCGc3rjM3wT0CNxBoAt+edEFkQyXxMrcOKdaQwJDSzm4BCEyUIGaH9U+7kF4HgiiC3cwcMYdcxBQEeAZ7WPKEYkQSHZZalQSKDMt95jveai+O/VoIcP07c7h8efi1TGkEtm4/PnlgcHWwWA9GHfAkF3YdnY5Z+Cm8UySgkjwgQQBCjlJTlEpfXCqpbfeJjAIMtEsHyEXBx9O9/YP/uy52DIutOqC8WAkpMg9EG55IjK6RJy0ioScksdJ02fXzmOP23tY3N7wVpjmhrfhS2Ni5bbym4CeiTtMdMGa8JnRTLNDElAIJANOfPKGOBEM0JRzcPIHtibUOgiqB+E+CF3wS4o+0RCw/xNjnkjrFXEOlabMIPlgmTNC/dggyHUQZA/CXRCEEF3YbwuaBcO4JMIgA1LYRLxSmZhMm/s/MzU5dOhgAETh2+kk+paN64CejVs2VBcsa2tMYjZwEpQ0qC15fGUNJ4lzZgHnCKtVh5aclhwwtgYG6yeNFTC64LcaQWYrdCDZR0ukT5ZYjvZ1lAk4BaG99q3AcPL+vhx5kRpUD8OTMBjpuuCrQR033nBDmDZxeVgA8S42RHibolY8eNkpDYpRQgVpXNCvD3y7DujZuGLDGms7cewYgPVeAlHaBiKh2bSjypEsKLciZ4hKdIsNvURBrRyGuAzo2bhlw3Vi386CjF55kmSUzf6EJ4EbSrilUgQOzNuWG9qN8XrPuihevds7fnH7dT0Pz+KhC/t1SonIcgoEbQg0KjLwZlsiEoEmt+IyBGPsj3cCWY/AMxHowvYczSJxcJZ4lTKRxmdiI9DmBAEvVYwhZ/Uj+rv2CDwLgS5szDmRQPqsSHQ5EcnBEG91JkkraTgD4M63XHO9t7YE1d7+P/9V4H8fsbWKV64YT4hSxddOqUiPx19/KMrOY09F2d1ZeRLN7s7tc252d+4+tejNfwFPLz8m/GgAAA=="]
                }
            }]
    }
}'
