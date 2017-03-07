import cPickle as pickle
import json

rows = []
files = ["sw1.json", "sw2.json", "jk1.json", "jk2.json"]

def loadRedditJSON(filename):
	f = open("reddit/" + filename)

	jsondata = f.read()
	f.close()

	jsondata = json.loads(jsondata)

	for child in jsondata["data"]["children"]:
		newobj = {}
		if not child["data"]["stickied"]:
			newobj["link"] = child["data"]["permalink"]
			newobj["data"] = [child["data"]["selftext"]]
			rows.append(newobj)


for file in files:
	loadRedditJSON(file)

pickle.dump(rows, open("redditdb.p", "wb"))
