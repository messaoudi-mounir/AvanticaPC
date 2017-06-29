====== MBE CEP Engine ======

This repository contains Petrolink's MBE CEP Engine built on Timbergrove's TREE CEP.

==== Branches ====

Keep branch names simple and short. Names should be all lowercase, using dashes to separate words. Most branches should
be based on the default branch.

default = Contains the latest development; may be unstable.

rel-    = Prefix for release branches based on default. The branch name should be in a format like "rel-1.2.3".
          This is where last minute stabilization and bug fixing will go for a release. Fixes done here should be
		  merged back into default. When the release is ready the changeset should be tagged with a version number.
		  
feat-   = Prefix for feature branches based on default. This is where new or existing features can be developed if they
          are too complex for the default branch or are to be included into a future version. It is recommended to try
		  to avoid creating feature branches. Instead continue development on the default branch so that features are
		  continuously integreated. Feature flags are recommended for testing new features at runtime.

junk-   = Prefix for throwaway junk branches used for experimentation or anything else.
