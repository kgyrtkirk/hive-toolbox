#!/bin/bash


#| 6               | Gourmet Supermarket  | 47               | Store 6           | 6                   | 5495 Mitchell Canyon Road   | Beverly Hills     | CA                 | 55555                    | USA                  | Maris                | 958-555-5002       | 958-555-5001     | 1981-01-03 00:00:00.0    | 1991-03-13 00:00:00.0    | 23688             | 15337               | 5011               | 3340             | true              | true               | true             | true                 | true           |

cat | sed -r "s/^\| /'/;s/ *\| */','/g"
