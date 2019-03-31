To run:
- Requires simple json library
- Requires jdbc library
- Requires MySQL tables to be set up
- Requires MySQL db permissions to connect to db on localhost.

Assumptions:
- Crew member is viewing surrounding sea from 6f above sea level, meaning the horizon is 6 miles, or 9.656...km (rounded up to 10km) away.
- Assumes anything over the horizon can't be seen
- Assumes that timestamps are given at regular intervals over time.

Function:
- Calculates the average number of boats a particular vessel will see each day during it's journey.
- Output is given in csv

Problems:
- As timestamps are not given at regular time intervals, my solution does not work
- As the solution to this requires knowledge of where boats are at any given timestamp during the duration of the race, it has not been implemented.
- The calculations necessary in order to do this are out of the scope of my solution, given the amount of time I currently can feasibly allow to work on the problem.
