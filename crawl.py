import dryscrape
import cPickle as pickle
import time
import random
from bs4 import BeautifulSoup
dryscrape.start_xvfb()
session = dryscrape.Session()

def getPostsInCategory(category):
	session.visit("https://www.awel.be/forum/themas/" + category)
	response = session.body()
	soup = BeautifulSoup(response, "html.parser")
	entries = []
	for entry in soup.find_all("a", {"class": "title"}):
		newentry = {}
		newentry["title"] = entry.text;
		newentry["link"] = entry["href"]
		entries.append(newentry)
	return entries

def extractPost(link):
	session.visit("https://www.awel.be" + link)
	response = session.body()
	soup = BeautifulSoup(response, "html.parser")
	entries = []
	for entry in soup.find_all("div", {"class": "body"}):
		newentry = entry.text;
		entries.append(newentry)
	return entries

def scrapeCategory(category):
	records = []
	posts = getPostsInCategory(category)
	i = 1
	for post in posts:
		print(str(i) + "/" + str(len(posts)))
		sleeptime = random.uniform(10,25)
		print("Scraping next post after " + str(round(sleeptime)) + " seconds")
		time.sleep(sleeptime)
		newentry = post
		newentry["data"] = extractPost(post["link"])
		records.append(newentry)
		i+=1
	return records

pickle.dump(scrapeCategory("voel-je-goed?page=20"), open("050320172058.p", "wb"))
#pickle.dump(getPostsInCategory("voel-je-goed"), open("postlist.p", "wb"))
