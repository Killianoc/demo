This is a Code Challenge to assign seating on an airplane. Satisfaction is judged based on
if travelling groups sit together, and whether people who requested a window seat get it.

Requirements:
* Java runtime environment
* Maven (if building/packaging the project)

I have assumed, based on the challenge PDF, that there are 2 windows per row. This is because
in the example input file, there was a 100% satisfaction rating output, even though both
individual "11" and individual "12" both requested window seats, 12 did not seemingly lose
any satisfaction. I have also assumed that if a group of two people are travelling together
and get to sit on the same row, they will be 100% satisfied, even if they are not sitting
right beside each other. They are still on the same row.

My approach involves parsing the file into various POJO's with which I can work with easily.
Once I have done this, I iterate through each "Potential travel group", and attempt to merge
the travel group with any other row depending on whether it is possible or not. This is repeated
until the rows remaining is less than or equal to the maximum rows allowed on the aircraft.

The program will not print in a desired order like is displayed in the problem statement PDF,
however it will attempt to keep all customers satisfied. So you could say the customer satisfaction
is preferred to the original order of where the customer was in the input file.

I used a Spring command line runner, along with a maven project as it gives me access
to a spring context, which can be quite useful for autowiring a service and other things.
It also allowed me to use the @Slf4j annotation for logging, which is convenient for quick
logging in the application.

Satisfaction factors:

100% Satisfaction - Person is sitting with all of original group and got desired seat
50% Satisfaction - Person is not sitting with original group, but got desired window seat
25% Satisfaction - Person is not sitting with original group, nor did they get their desired seat. Lucky to get on plane.

How to run:

Assuming maven is installed -
1. In the directory where "pom.xml" is, open a command prompt and type "mvn clean package"
2. Move to the "target" directory, and then in the command "java -jar demo-1.0.0.jar C:/path/to/file"
3. Watch the output in the terminal window.

If maven is not installed, use the existing jar file in the directory "demo".
1. Type "java -jar demo-1.0.0.jar C:/path/to/file" in the terminal window.
2. Watch the output in the terminal window.

Possible improvements:
* Some parallel performance improvements could be made to enhance speed.
* Satisfaction based on who you are sitting beside could be implemented.
* Some code refactoring to make it clearer