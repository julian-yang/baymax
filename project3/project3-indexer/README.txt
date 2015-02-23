Part A2: Lucene Indexing
-ItemID has been indexed/stored as an IntField, according to its database type (optionally, this can be made into a StringField, since the skeleton code uses "String itemId" by default)
-Name has been indexed/stored as a StringField, since its contents need not be tokenized here
-Content (which concatenates ItemID, Name, Description, and multiple Category values, complete with whitespace) has been indexed but not stored as a TextField, since it is used in search but not returned to the user
