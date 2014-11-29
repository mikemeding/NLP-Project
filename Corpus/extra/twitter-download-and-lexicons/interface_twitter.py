
import os
import sys
import pickle
import urllib2

from twitter import oauth_dance, read_token_file, Twitter, OAuth, TwitterError
import twitter.api


# TODO - lookup real maximum
maxTweets = 20000


class Cache:

    def __init__(self):
        self.filename = os.path.join(os.getenv('TWITERATE_DIR'),'etc/cache')

        # Old lookups
        try:
            self.cache = pickle.load( open( self.filename ,"rb" ) )
        except IOError:
            self.cache = {}

        # New lookups
        self.new = {}


    # Memoization decorator
    def memoize(self, function):
        memo = self.cache
        memo.update(self.new)
        kwd_mark = '<keyword-marker>'
        def wrapper(*args, **kwargs):
            key = args[1:] # Ignore first argument, which is a TwitterInterface object
            if kwargs:
                key += (kwd_mark,) + tuple(sorted(kwargs.items()))
            #print 'key: ', key
            if key in memo:
                #print '\tHIT'
                return memo[key]
            else:
                #print '\tMISS'
                rv = function(*args, **kwargs)
                self.new[key] = rv
                return rv
        return wrapper

    def __del__(self):
        import os, pickle
        #print '\tcache destructor (%s)' % os.path.basename(self.filename)

        # Only re-pickle object if there's anything new to pickle
        if len(self.new):
            #print 'dumping'
            total = self.cache
            total.update(self.new)
            pickle.dump(total, open(self.filename,"wb"))


# Instantiate cache
cache = Cache()
memoize = cache.memoize


class TwitterInterface(Twitter):

    def __init__(self, connect=True):

        # Offline?
        if connect:
            # Twitter credentials
            CONSUMER_KEY='JEdRRoDsfwzCtupkir4ivQ'
            CONSUMER_SECRET='PAbSSmzQxbcnkYYH2vQpKVSq2yPARfKm0Yl6DrLc'

            MY_TWITTER_CREDS = os.path.expanduser('~/.my_app_credentials')
            if not os.path.exists(MY_TWITTER_CREDS):
                oauth_dance("Semeval sentiment analysis", CONSUMER_KEY, CONSUMER_SECRET, MY_TWITTER_CREDS)
            oauth_token, oauth_secret = read_token_file(MY_TWITTER_CREDS)
            self.t = Twitter(auth=OAuth(oauth_token, oauth_secret, CONSUMER_KEY, CONSUMER_SECRET))


    @memoize
    def statuses_user_timeline(self, *args, **kwargs):
        try:
            return self.t.statuses.user_timeline(*args, **kwargs)
        except urllib2.URLError, e:
            print >>sys.stderr, 'TwiiterInterface::statuses_user_timeline() failed'
            print >>sys.stderr, 'Ensure you are connected to Twitter API'
            exit(1)
        except twitter.api.TwitterHTTPError:
            print >>sys.stderr, 'TwiiterInterface::statuses_user_timeline() error'
            print >>sys.stderr, 'Bad Query: ', args, kwargs
            exit(1)


    @memoize
    def search_tweets(self, *args, **kwargs):
        return self.t.search.tweets(*args, **kwargs)


    def __del__(self):
        cache.__del__()