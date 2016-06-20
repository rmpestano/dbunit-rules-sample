Feature: Search users
In order to find users quickly
As a recruiter
I want to be able to query users by meaningful search criteria.

Scenario Outline: Search users by tweet content

Given We have two users that have tweets in our database

When I search them by tweet content <value>

Then I should find <number> users
Examples:
| value    | number |
| "dbunit" | 1      |
| "rules"  | 2      |