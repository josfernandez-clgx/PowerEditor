# Invoke a servlet

# Assumes the traditional url pattern http://<server>/<application>/servlet/<servlet-name>

use LWP::UserAgent;
use HTTP::Request;

# Parameters
my $host = $ARGV[0];
my $port = $ARGV[1];
my $app = $ARGV[2];
my $servlet = $ARGV[3];
my $queryStr = $ARGV[4];

my $url = "http://$host:$port/$app/servlet/$servlet?$queryStr";

# Invoke servlet
my $user_agent = LWP::UserAgent->new;
$user_agent->timeout(600); # 10 minutes

my $request = HTTP::Request->new(GET => $url);
my $response = $user_agent->simple_request($request);

if ($response->is_error) {
    die $response->status_line;
}

# Print response to stdout
print $response->content, "\n";

exit 0;
