# Data Scientist Assessment
## Author: Tran Ha Ngoc Thien


### Please read me first!!! Important information is here!!!

I. Project Contructor information
1. Environment
	- OS: this project was built on Window 10 Enterprise (due to the lack of linux/ubuntu on my personal computer)
	- IDE: intellij IDE 2018.3.1
	- Language: Java 8
	- JDK version: 1.8.0_172 (with javafx included)
2. Contructor
	- pom.xml: belong to maven, do not remove it! Because of using maven inside this project
	- app.config: configurations of application which built by this project
		+ model_path: set the path of file where trained model will be stored
	- .idea: configuration folder of intellij idea
	- src/main/java: source code of project
	- src/main/test/java: source code of unit test (I'm using junit for this project)
3. How to use?
	- Just build and run the source code!

II. Project Conclusion
   For the second task, I stuck on the trouble that the data contains a really large bag of words. This problem prevents me to build one model for whole data. 
   I had found some solutions for this kind of obstacle however the point of the task that is using pca and tf-idf (which bases on words to process). 
   Therefore I splitted the training data into small pieces and trained on those minor datas. Of course this approach would be too far to the destination but this is the way I can handle. 
   May be my approach for the implementation of algorithm was wrong (tf-idf could be apply not for word but something else or pca has some techniques to handle this situation that I don't know).