#!/usr/bin/perl

my $OLD_PREFIX="org\.nyc\.cir\\^ICE\\^1\.0\.0";
my $REPLACE_PREFIX="org\.nyc\.cir\^ICE\^1\.1\.0";


my $dir = '.';

opendir(DIR, $dir) or die $!;

while (my $file = readdir(DIR)) {

    if (-d "$dir/$file") {
	next;
    }
    # Use a regular expression to ignore files beginning with a period
    # if ($file =~ m/^org\.nyc/) {
    if ($file =~ m/^$OLD_PREFIX/) {
	print "$file\n";

	my $newname = $file;
        $newname =~ s/$OLD_PREFIX/$REPLACE_PREFIX/;
	print "$newname\n";
        rename $file, $newname;
    }
}

closedir(DIR);
exit 0;
