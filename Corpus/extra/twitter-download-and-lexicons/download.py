
from interface_twitter import TwitterInterface, maxTweets


def main():

    # Create object that allows me to communicate with Twitter
    twobject = TwitterInterface(connect=True)

    # Get a particular friend's timeline
    tweets1 = twobject.statuses_user_timeline(screen_name="WilliamBoag"    , count=maxTweets)

    print tweets1

    exit()

    for twt in tweets1:
        if twt['favorite_count'] > 0:
            print twt['text']
            print '-'*40



if __name__ == '__main__':
    main()




#tweets2 = twobject.statuses_user_timeline(screen_name="WillBoagBaggins", count=maxTweets)
#tweets3 = twobject.statuses_user_timeline(screen_name="FuchsiaKnight"  , count=maxTweets)
#tweets4 = twobject.statuses_user_timeline(screen_name="BillGates"      , count=maxTweets)

