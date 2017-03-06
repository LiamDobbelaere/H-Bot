import string
import copy
import os
import cPickle as pickle

#ignore_words = ["je", "jouw", "mijn", "me", "ik", "maar", "jij", "jou", "ben", "bent", "heb", "hebt", "kan", "awel", "jullie", "en", "want"]
ignore_words = []
hct = 50 #Header cleanup tolerance

r = []

print "Loading files.."
files = [f for f in os.listdir('.') if os.path.isfile(f)]
for f in files:
	f = f.split('.')[0]
	if f.isdigit():
		print "Loading " + f
		a = pickle.load(open(f + ".p", "rb"))
		r += a

print "Converting to dictionary.."

kv = {}

for item in r:
	kv[item["link"]] = item["data"]

#keys = kv.keys()
#print kv[keys[0]]

i = 0

def translate_non_alphanumerics(to_translate, translate_to=u'_'):
    not_letters_or_digits = u'!"#%\'()*+,-./:;<=>?@[\]^_`{|}~'
    translate_table = dict((ord(char), translate_to) for char in not_letters_or_digits)
    return to_translate.translate(translate_table)

def cleanString(stringv):
	stringv = translate_non_alphanumerics(stringv.lower(), None)
	return stringv

def binarizeText(text, cl):
	text = cleanString(text)
	text = text.split(' ')
	text = filter(None, text)
	newobj = copy.copy(headers)
	for word in text:
		if word in newobj:
			newobj[word] = 1
	newobj["class"] = cl
	return newobj

def processText(text):
	text = cleanString(text)
	text = text.split(' ')
	text = filter(None, text)
	for word in text:
		if word in headers:
			headers[word] += 1
		else:
			headers[word] = 1

def cleanupHeaders(tolerance):
	tolval = len(rows) * (tolerance / 100.0)
	
	toremove = []
	for key in headers:
		if headers[key] <= tolerance or key in ignore_words or headers[key] == 0:
			toremove.append(key)

	for item in toremove:
		headers.pop(item, None)
	
	print str(len(toremove)) + " headers cleaned with tolerance of " + str(tolerance) + "%"

def setHeadersToZero():
	for key in headers:
		headers[key] = 0

headers = {}
rows = []

for key in kv:
	value = kv[key]
	if i == 0:
		if len(value) > 1:
			request = value[0]
			response = value[1]

			processText(request)
			processText(response)
	#i+=1	
print "Cleaning up headers with " + str(hct) + "% tolerance"
cleanupHeaders(hct)
headers["class"] = 0
setHeadersToZero()

for key in kv:
	value = kv[key]
	if len(value) > 2:
		request = value[0]
		response = value[2]
		
		reqb = binarizeText(request, 1)
		resb = binarizeText(response, 0)

		rows.append(reqb)
		rows.append(resb)

def removeUselessRows():
	urtol = int(input("Useless rows tolerance: "))
	uselessrows = []

	for row in rows:
		usefulvalues = 0
		for item in row:
			if row[item] == 1:
				usefulvalues += 1

		if usefulvalues < len(headers) * (urtol / 100.0):
			uselessrows.append(row)

	print "Removing " + str(len(uselessrows)) + " useless rows"

	for ind in uselessrows:
		rows.remove(ind)

#cleanupHeaders(cleanuptol)

#for key in rows[0]:
	#if rows[0][key] == 1:
		#print key, rows[0][key]

orderedheaders = []

for key in headers:
	orderedheaders.append(key)

orderedheaders.sort()
orderedheaders.remove("class")
orderedheaders.append("class")

print("Writing to helpdb.arff")

f = open('helpdb.arff', 'w')

f.write("@RELATION helpdb\n")
f.write("\n")
for key in orderedheaders:
	f.write("@ATTRIBUTE " + key.encode("utf-8") + " {0,1}\n")
f.write("\n")
f.write("@data\n")
for item in rows:
	f.write(str(item[orderedheaders[0]]))
	for key in orderedheaders[1:]:
		#if orderedheaders.index(key) == len(orderedheaders) - 1:
		#	f.write(str(item[key]))	
		#else:
		f.write("," + str(item[key]))
	f.write("\n")

f.close()

print("Finished, wrote " + str(len(rows)) + " rows")
