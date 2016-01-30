#!/usr/bin/env ruby

#
# release.rb: Prepares a Cook-E release and publishes it
#
# Usage: release.rb version
#
# version: A semantic version number, with an optional alpha, beta, or rc suffix
# and without a 'v' prefix
#


# Modifies the build.gradle file at the provided path.
# Increments the version code and sets the version name to the provided version.
def update_build_file(path, version)

    version_code_regex = /^\s*versionCode\s+(\d+)$/
    version_name_regex = /^\s*versionName\s+"/

    lines = []

    File.open(path) do |file|
        lines = file.readlines
    end

    lines.map! do |line|
        version_code_match = version_code_regex.match line
        if version_code_match
            # Replace this line with the new version code
            old_code = version_code_match[1].to_i;
            new_code = old_code + 1;

            puts "Increasing version code from #{old_code} to #{new_code}"

            # Return the new line
            "        versionCode #{new_code}\n"
        elsif line =~ version_name_regex
            # Replace this line with the new version name
            "        versionName \"#{version}\"\n"
        else
            # Preserve this line
            line
        end
    end

    # Write the modified lines to the file
    File.open(path, File::WRONLY | File::TRUNC) do |file|
        lines.each do |line|
            file.write line
        end
    end
end

# Validate the version number
version = ARGV[0]

if !version || version.empty?
    puts 'Please specify a version number.'
    exit -1
end

VERSION_REGEX = /^\d+.\d+.\d+(-(alpha|beta|rc)\d+)?$/

if !(version =~ VERSION_REGEX)
    puts "#{version} is not a valid version number."
    puts 'Version numbers should not start with \'v\'.'
    exit -1
end

# Get the absolute path of the repository root folder
repo_root = `git rev-parse --show-toplevel`

if !repo_root || repo_root.empty?
    puts 'Could not find the repository root. Please ensure the current directory is in the project folder.'
    exit -1
end
# Remove whitespace
repo_root.strip!

# Validate git status
branch = `git rev-parse --abbrev-ref HEAD`
branch.strip!
if branch != 'master'
    puts "You are currently viewing branch #{branch}. All new releases should be made from master."
    exit -1
end

status = `git status --porcelain`
if !status.empty?
    puts 'Your working directory is not clean. Please commit all your changes.'
    exit -1
end

# ==============================================================================
# Start making changes

# Update versions in build file
update_build_file("#{repo_root}/app/build.gradle", version)


# Build APK
puts 'Building...'

result = system "#{repo_root}/gradlew assembleRelease"

if !result
    puts 'Build failed - release cancelled'
    exit -1
end

# Give the APK the correct name
apk_path = "#{repo_root}/app/build/outputs/apk/Cook-E-#{version}.apk"
File.rename("#{repo_root}/app/build/outputs/apk/app-release-unsigned.apk",
    apk_path)

# Commit changes
result = system "git add #{repo_root}/app/build.gradle"
if !result
    puts 'Failed to add file - release cancelled'
    exit -1
end
result = system "git commit -m 'Version increased to v#{version}'"
if !result
    puts 'Failed to commit - release cancelled'
    exit -1
end
# Make a tag
tag_name = "v#{version}"
result = system "git tag -a -m 'Version #{version}' #{tag_name}"
if !result
    puts 'Failed to create tag - release cancelled'
    exit -1
end
# Push
result = system "git push --tags"
if !result
    puts 'Failed to push - release cancelled'
    exit -1
end

# Display release information
puts ''
puts 'Now create a release: https://github.com/Cook-E-team/Cook-E/releases/new'
puts "Tag: #{tag_name}"
puts "Upload the APK file at #{apk_path}"

puts 'Done'
