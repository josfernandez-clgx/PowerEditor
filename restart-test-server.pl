# Restart an ESP cluster

use IO::Socket;

# Parameters
my $esp_host = "localhost";
my $esp_port = 1945;
my $esp_clst = "REF";                # ESP ClusterName
my @esp_svrs = ("1", "2", "3");      # ESP Server Names
my $preload_wait = 10;               # Seconds to wait after shutdown before issueing load commands
my $postload_wait = 30;              # Seconds to wait between queries for 'Accepting' status
my $status_retries = 10;             # Number of times to try status query before failing
my $retry_attempts = 0;

# Constants
my $END_OF_TEXT = pack("U*", 0x03);  # Character that completes transmission to/from server 

# sub routines
sub openSocket {
	my $conn = IO::Socket::INET->new(
	  Proto => "tcp",
	  PeerAddr => $esp_host,
	  PeerPort => "$esp_port",
	  Type => SOCK_STREAM,
	  Timeout => 45
	) or die "failed to open socket.\n";

	return $conn;
}

sub sendCmd {
	(my $conn, my $cmd_suffix) = @_;

	my $cmd_str = "ESPControlRequest~AutomatedBuild|$cmd_suffix";
	print "Sending cmd $cmd_str.\n";
	print $conn $cmd_str;
	print $conn $eot;
}

sub sendCmdAndClose {
	(my $conn, my $cmd_suffix) = @_;

	sendCmd($conn, $cmd_suffix);
	close($conn);
}

sub clusterAvailable {
	my $conn = openSocket();

	sendCmd($conn, "status-brief~cluster~$esp_clst");

	while(($line = <$conn>) && ($line !~ /Current State/i)) {}

	close($conn);

	return ($line =~ /Accepting/i);
}

sub startSrvrs {
	foreach $srvr_name (@esp_svrs) {
		sendCmdAndClose(openSocket(), "load~service~$esp_clst~$srvr_name");
	}

	STATUS_RETRY: for($retry_attempts = 0; $retry_attempts < $status_retries; $retry_attempts++) {
		print "Pausing for $postload_wait seconds while ESP starts up services ($retry_attempts).\n";
		sleep $postload_wait;
		last STATUS_RETRY if clusterAvailable();
	}
}

sub clearErrorsAndRetry {
	print "Cluster never came back up, trying to recover by clearing errors.\n";
	foreach $srvr_name (@esp_svrs) {
		sendCmdAndClose(openSocket(), "clear-error~service~$esp_clst~$srvr_name");
	}

	startSrvrs();	
	
	if ($retry_attempts == $status_retries) {
		print "Cluster never came back up and retry failed, exiting.\n";
		exit 1;
	}	
}

# Processing starts here

# Shutdown
sendCmdAndClose(openSocket(), "shutdown~cluster~$esp_clst");

# Pause
print "Pausing for $preload_wait seconds while ESP shuts down cluster.\n";
sleep $preload_wait;

# Restart
startSrvrs();

# If failed, Retry
if ($retry_attempts == $status_retries) {
	clearErrorsAndRetry();
}

# Success
print "Cluster is Accepting again.\n";
exit 0;
