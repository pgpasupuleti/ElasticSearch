# ElasticSearch
Elastic Search Basic Setup


### mapper-attachments
refer https://github.com/elastic/elasticsearch-mapper-attachments

install elasticsearch-mapper-attachments plugin to elastic search using following command

bin/plugin install elasticsearch/elasticsearch-mapper-attachments/2.5.0


Once this plugin installed, start the elastic search server.  

mapping format:

```json
{
    "attachment": {
        "properties": {
            "file": {
                "type": "attachment",
                "path": "full",
                "fields": {
                    "title": {
                        "type": "string",
                        "fields": {
                            "suggest": {
                                "type": "string",
                                "store": "yes"
                            }
                        }
                    },
                    "file": {
                        "term_vector": "with_positions_offsets",
                        "store": "yes"
                    },
                    "date": {
                        "type": "date",
                        "fields": {
                            "string": {
                                "type": "string"
                            }
                        }
                    },
                    "name": {
                        "type": "string",
                        "fields": {
                            "suggest": {
                                "type": "string",
                                "store": "true"
                            }
                        }
                    },
                    "keywords": {
                        "type": "string",
                        "fields": {
                            "suggest": {
                                "type": "string"
                            }
                        }
                    },
                    "content_type": {
                        "type": "string"
                    },
                    "metadata": {
                        "type": "string",
                        "fields": {
                            "suggest": {
                                "type": "string"
                            }
                        }
                    }
                }
            }
        }
    }
}
```

